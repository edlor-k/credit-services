package ru.creditservices.deal.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.mapper.LoanOfferMapper;
import ru.creditservices.deal.mapper.LoanStatementMapper;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.service.impl.CreateLoanStatementServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreateLoanStatementServiceImplTest {

    @Mock private LoanStatementMapper loanStatementMapper;
    @Mock private LoanOfferMapper loanOfferMapper;
    @Mock private ClientManagerService clientManagerService;
    @Mock private StatementManagerService statementManagerService;
    @Mock private CalculatorClientService calculatorClientService;

    @InjectMocks
    private CreateLoanStatementServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(loanStatementMapper, loanOfferMapper,
                clientManagerService, statementManagerService, calculatorClientService);
    }

    @Test
    @DisplayName("Должен создаваться клиент, заявка и возвращаться список предложений по кредиту")
    void getLoanOffersShouldCreateClientAndStatementAndReturnOffers() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        LoanStatementEntity statementEntity = new LoanStatementEntity();
        LoanOfferDto offerDto = LoanOfferDto.builder().build();
        LoanOfferEntity offerEntity = LoanOfferEntity.builder().build();
        ClientEntity clientEntity = ClientEntity.builder().clientId(UUID.randomUUID()).build();
        StatementEntity statement = StatementEntity.builder().statementId(UUID.randomUUID()).build();

        when(loanStatementMapper.toEntity(requestDto)).thenReturn(statementEntity);
        when(calculatorClientService.fetchLoanOffers(requestDto)).thenReturn(List.of(offerDto));
        when(loanOfferMapper.toEntity(offerDto)).thenReturn(offerEntity);
        when(clientManagerService.createClient(statementEntity)).thenReturn(clientEntity);
        when(statementManagerService.createStatementFromClient(clientEntity)).thenReturn(statement);
        when(loanOfferMapper.toDto(any())).thenReturn(offerDto);

        List<LoanOfferDto> result = service.getLoanOffers(requestDto);

        assertThat(result).hasSize(1);
        verify(loanStatementMapper).toEntity(requestDto);
        verify(calculatorClientService).fetchLoanOffers(requestDto);
        verify(clientManagerService).createClient(statementEntity);
        verify(statementManagerService).createStatementFromClient(clientEntity);
        verify(loanOfferMapper, atLeastOnce()).toEntity(offerDto);
        verify(loanOfferMapper, atLeastOnce()).toDto(any());
    }
}