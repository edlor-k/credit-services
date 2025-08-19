package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.ErrorResponseDto;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.ScoringDataDto;
import ru.creditservices.deal.exception.CalculatorServiceException;
import ru.creditservices.deal.exception.CreditAlreadyExistException;
import ru.creditservices.deal.mapper.CreditMapper;
import ru.creditservices.deal.mapper.FinishRegistrationMapper;
import ru.creditservices.deal.mapper.ScoringDataMapper;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.model.enums.ErrorCode;
import ru.creditservices.deal.service.impl.CalculateFinalParametersServiceImpl;
import ru.creditservices.deal.service.impl.ScoringDataAssembler;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateFinalParametersServiceImplTest {

    @Mock private ScoringDataMapper scoringDataMapper;
    @Mock private CreditMapper creditMapper;
    @Mock private FinishRegistrationMapper finishRegistrationMapper;
    @Mock private StatementManagerService statementManagerService;
    @Mock private CreditManagerService creditManagerService;
    @Mock private ScoringDataAssembler scoringDataAssembler;
    @Mock private ClientManagerService clientManagerService;
    @Mock private CalculatorClientService calculatorClientService;

    @InjectMocks
    private CalculateFinalParametersServiceImpl service;

    private final UUID statementId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Mockito.reset(
                scoringDataMapper, creditMapper, finishRegistrationMapper, statementManagerService,
                creditManagerService, scoringDataAssembler, clientManagerService, calculatorClientService
        );
    }

    @Test
    @DisplayName("Расчет финальных параметров: успешное одобрение кредита")
    void calculateFinalParameters_shouldHandleApproved() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        ScoringDataDto scoringDto = mock(ScoringDataDto.class);
        CreditDto creditDto = mock(CreditDto.class);
        CreditEntity creditEntity = mock(CreditEntity.class);

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(statementId)).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(null);
        when(scoringDataAssembler.assembleScoringDataEntity(statementEntity, finishEntity)).thenReturn(scoringData);
        when(scoringDataMapper.toDto(scoringData)).thenReturn(scoringDto);
        when(calculatorClientService.fetchCalculatorResult(scoringDto)).thenReturn(creditDto);
        when(creditMapper.toEntity(creditDto)).thenReturn(creditEntity);

        service.calculateFinalParameters(statementId, finishDto);

        verify(creditManagerService).createCreditFromCreditEntity(creditEntity);
        verify(statementManagerService).updateStatementFromScoringData(scoringData, statementId);
        verify(statementManagerService).addCreditToStatement(statementId, creditEntity);
        verify(clientManagerService).updateClientInformationFromScoringData(scoringData);

        verify(statementManagerService, never()).setLoanWaiver(any());
    }

    @Test
    @DisplayName("Расчет финальных параметров: отказ по бизнес-правилам -> CalculatorServiceException")
    void calculateFinalParameters_shouldPropagateBusinessDeclineAsException() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        ScoringDataDto scoringDto = mock(ScoringDataDto.class);

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(statementId)).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(null);
        when(scoringDataAssembler.assembleScoringDataEntity(statementEntity, finishEntity)).thenReturn(scoringData);
        when(scoringDataMapper.toDto(scoringData)).thenReturn(scoringDto);

        ErrorResponseDto decline = ErrorResponseDto.builder()
                .code(ErrorCode.CLIENT_ERROR)
                .message("Отказ по бизнес-правилам")
                .details(Map.of("business", "Отказ по бизнес-правилам"))
                .build();

        when(calculatorClientService.fetchCalculatorResult(scoringDto))
                .thenThrow(new CalculatorServiceException(decline));

        assertThatThrownBy(() -> service.calculateFinalParameters(statementId, finishDto))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("Отказ по бизнес-правилам");

        verifyNoInteractions(creditManagerService, clientManagerService);
        verify(statementManagerService, never()).setLoanWaiver(any());
        verify(statementManagerService, never()).addCreditToStatement(any(), any());
        verify(statementManagerService, never()).updateStatementFromScoringData(any(), any());
    }

    @Test
    @DisplayName("Расчет финальных параметров: ошибка запроса/валидации -> CalculatorServiceException")
    void calculateFinalParameters_shouldThrowOnRequestError() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);
        ScoringDataEntity scoringData = mock(ScoringDataEntity.class);
        ScoringDataDto scoringDto = mock(ScoringDataDto.class);

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(statementId)).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(null);
        when(scoringDataAssembler.assembleScoringDataEntity(statementEntity, finishEntity)).thenReturn(scoringData);
        when(scoringDataMapper.toDto(scoringData)).thenReturn(scoringDto);

        ErrorResponseDto err = ErrorResponseDto.builder()
                .code(ErrorCode.INVALID_ARGUMENT)
                .message("Ошибка запроса")
                .details(Map.of("field", "Некорректное значение"))
                .build();

        when(calculatorClientService.fetchCalculatorResult(scoringDto))
                .thenThrow(new CalculatorServiceException(err));

        assertThatThrownBy(() -> service.calculateFinalParameters(statementId, finishDto))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("Ошибка запроса");

        verifyNoInteractions(creditManagerService, clientManagerService);
        verify(statementManagerService, never()).addCreditToStatement(any(), any());
        verify(statementManagerService, never()).updateStatementFromScoringData(any(), any());
        verify(statementManagerService, never()).setLoanWaiver(any());
    }

    @Test
    @DisplayName("Расчет финальных параметров: выбросить исключение, если кредит уже существует")
    void calculateFinalParameters_shouldThrowCreditAlreadyExistException() {
        FinishRegistrationRequestDto finishDto = mock(FinishRegistrationRequestDto.class);
        FinishRegistrationEntity finishEntity = mock(FinishRegistrationEntity.class);
        StatementEntity statementEntity = mock(StatementEntity.class);

        when(finishRegistrationMapper.toEntity(finishDto)).thenReturn(finishEntity);
        when(statementManagerService.getStatementOrThrow(statementId)).thenReturn(statementEntity);
        when(statementEntity.getCredit()).thenReturn(mock(CreditEntity.class));

        assertThatThrownBy(() -> service.calculateFinalParameters(statementId, finishDto))
                .isInstanceOf(CreditAlreadyExistException.class);

        verifyNoInteractions(creditManagerService, clientManagerService);
        verify(calculatorClientService, never()).fetchCalculatorResult(any());
    }
}
