package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.exception.CalculatorValidationException;
import ru.creditservices.deal.exception.CreditAlreadyExistException;
import ru.creditservices.deal.mapper.CreditMapper;
import ru.creditservices.deal.mapper.FinishRegistrationMapper;
import ru.creditservices.deal.mapper.ScoringDataMapper;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.service.impl.CalculateFinalParametersServiceImpl;
import ru.creditservices.deal.service.impl.ScoringDataAssembler;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateFinalParametersServiceImplTest {

    @Mock
    private ScoringDataMapper scoringDataMapper;

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private FinishRegistrationMapper finishRegistrationMapper;

    @Mock
    private StatementManagerService statementManagerService;

    @Mock
    private CreditManagerService creditManagerService;

    @Mock
    private ScoringDataAssembler scoringDataAssembler;

    @Mock
    private ClientManagerService clientManagerService;

    @Mock
    private CalculatorClientService calculatorClientService;

    @InjectMocks
    private CalculateFinalParametersServiceImpl service;

    private final String statementId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        Mockito.reset(
                scoringDataMapper, creditMapper, finishRegistrationMapper, statementManagerService,
                creditManagerService, scoringDataAssembler, clientManagerService, calculatorClientService
        );
    }

    @Test
    @DisplayName("Расчет финальных параметров должен корректно обработать одобренный кредит")
    void calculateFinalParametersShouldHandleApproved() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        ScoringDataDto scoringDto = mock(ScoringDataDto.class);
        CreditDto creditDto = mock(CreditDto.class);
        CreditEntity creditEntity = mock(CreditEntity.class);

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(UUID.fromString(statementId))).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(null);
        when(scoringDataAssembler.assembleScoringDataEntity(statementEntity, finishEntity)).thenReturn(scoringData);
        when(scoringDataMapper.toDto(scoringData)).thenReturn(scoringDto);
        when(calculatorClientService.fetchCalculatorResult(scoringDto)).thenReturn(CalculatorResult.approved(creditDto));
        when(creditMapper.toEntity(creditDto)).thenReturn(creditEntity);

        service.calculateFinalParameters(statementId, finishDto);

        verify(creditManagerService).createCreditFromCreditEntity(creditEntity);
        verify(statementManagerService).updateStatementFromScoringData(scoringData, UUID.fromString(statementId));
        verify(statementManagerService).addCreditToStatement(UUID.fromString(statementId), creditEntity);
        verify(clientManagerService).updateClientInformationFromScoringData(scoringData);
    }

    @Test
    @DisplayName("Расчет финальных параметров должен корректно обработать отказ по бизнес-правилам")
    void calculateFinalParametersShouldHandleBusinessDecline() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        ScoringDataDto scoringDto = mock(ScoringDataDto.class);

        String declineReason = "Отказ по бизнес-правилам";
        List<Violation> violations = List.of();

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(UUID.fromString(statementId))).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(null);
        when(scoringDataAssembler.assembleScoringDataEntity(statementEntity, finishEntity)).thenReturn(scoringData);
        when(scoringDataMapper.toDto(scoringData)).thenReturn(scoringDto);
        when(calculatorClientService.fetchCalculatorResult(scoringDto)).thenReturn(
                CalculatorResult.businessDecline(declineReason, violations)
        );

        service.calculateFinalParameters(statementId, finishDto);

        verify(statementManagerService).setLoanWaiver(UUID.fromString(statementId));
        verifyNoInteractions(creditManagerService);
        verifyNoInteractions(clientManagerService);
    }

    @Test
    @DisplayName("Корректная обработка ошибки запроса калькулятора")
    void calculateFinalParametersShouldThrowOnRequestError() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        ScoringDataDto scoringDto = mock(ScoringDataDto.class);

        String errorMsg = "Ошибка запроса";
        List<Violation> violations = List.of(new Violation("field", errorMsg));

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(UUID.fromString(statementId))).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(null);
        when(scoringDataAssembler.assembleScoringDataEntity(statementEntity, finishEntity)).thenReturn(scoringData);
        when(scoringDataMapper.toDto(scoringData)).thenReturn(scoringDto);
        when(calculatorClientService.fetchCalculatorResult(scoringDto)).thenReturn(
                CalculatorResult.requestError(errorMsg, violations)
        );

        assertThatThrownBy(() ->
                service.calculateFinalParameters(statementId, finishDto)
        ).isInstanceOf(CalculatorValidationException.class)
                .hasMessageContaining(errorMsg);

        verify(statementManagerService, never()).setLoanWaiver(any());
        verify(creditManagerService, never()).createCreditFromCreditEntity(any());
        verify(clientManagerService, never()).updateClientInformationFromScoringData(any());
    }

    @Test
    @DisplayName("Расчет финальных параметров должен выбросить исключение, если кредит уже существует")
    void calculateFinalParametersShouldThrowCreditAlreadyExistException() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(UUID.fromString(statementId))).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(mock(CreditEntity.class));

        assertThatThrownBy(() ->
                service.calculateFinalParameters(statementId, finishDto)
        ).isInstanceOf(CreditAlreadyExistException.class);

        verifyNoInteractions(creditManagerService);
        verifyNoInteractions(clientManagerService);
        verify(calculatorClientService, never()).fetchCalculatorResult(any());
    }
}