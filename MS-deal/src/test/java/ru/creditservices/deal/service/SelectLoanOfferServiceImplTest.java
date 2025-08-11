package ru.creditservices.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import ru.creditservices.deal.dto.EmailMessageDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.mapper.LoanOfferMapper;
import ru.creditservices.deal.model.entity.LoanOfferEntity;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.service.impl.SelectLoanOfferServiceImpl;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SelectLoanOfferServiceImplTest {

    @Mock
    private LoanOfferMapper loanOfferMapper;

    @Mock
    private StatementManagerService statementManagerService;

    @Mock
    private EmailMessageFactory emailMessageFactory;

    @Mock
    private KafkaEmailService kafkaEmailService;

    @InjectMocks
    private SelectLoanOfferServiceImpl selectLoanOfferService;

    private LoanOfferDto loanOfferDto;
    private LoanOfferEntity loanOfferEntity;
    private EmailMessageDto emailMessageDto;

    @BeforeEach
    void setUp() {
        UUID statementId = UUID.randomUUID();

        loanOfferDto = LoanOfferDto.builder()
                .statementId(statementId)
                .build();

        loanOfferEntity = LoanOfferEntity.builder()
                .statementId(statementId)
                .build();

        emailMessageDto = EmailMessageDto.builder()
                .statementId(statementId)
                .theme(EmailTheme.FINISH_REGISTRATION)
                .address("test@example.com")
                .text("Please finish registration")
                .build();
    }

    @Test
    void selectLoanOffer_shouldMapEntitySaveToStatementAndSendKafkaEmail() {
        when(loanOfferMapper.toEntity(loanOfferDto)).thenReturn(loanOfferEntity);
        when(emailMessageFactory.buildEmailMessage(loanOfferDto.getStatementId(), EmailTheme.FINISH_REGISTRATION,
                null))
                .thenReturn(emailMessageDto);

        selectLoanOfferService.selectLoanOffer(loanOfferDto);

        verify(loanOfferMapper).toEntity(loanOfferDto);
        verify(statementManagerService).selectLoanOfferToStatement(loanOfferEntity);
        verify(emailMessageFactory).buildEmailMessage(loanOfferDto.getStatementId(), EmailTheme.FINISH_REGISTRATION,
                null);
        verify(kafkaEmailService).sendMessage(emailMessageDto);

        verifyNoMoreInteractions(loanOfferMapper, statementManagerService, emailMessageFactory, kafkaEmailService);
    }
}
