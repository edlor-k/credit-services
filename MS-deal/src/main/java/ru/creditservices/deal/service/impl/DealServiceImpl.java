package ru.creditservices.deal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.factory.EmailMessageFactory;
import ru.creditservices.deal.model.enums.EmailTheme;
import ru.creditservices.deal.service.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final CalculateFinalParametersService calculateFinalParametersService;
    private final CreateLoanStatementService createLoanStatementService;
    private final SelectLoanOfferService selectLoanOfferService;
    private final KafkaEmailService kafkaEmailService;
    private final EmailMessageFactory emailMessageFactory;

    public List<LoanOfferDto> createLoanStatement(LoanStatementRequestDto dto) {
        return createLoanStatementService.getLoanOffers(dto);
    }

    public void selectLoanOffer(LoanOfferDto dto) {
        selectLoanOfferService.selectLoanOffer(dto);
    }

    public void calculateFinalLoanParameters(UUID statementId, FinishRegistrationRequestDto dto) {
        calculateFinalParametersService.calculateFinalParameters(statementId, dto);
    }

    public void sendDocuments(UUID statementId) {
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.CREATE_DOCUMENTS);
        kafkaEmailService.sendMessage(message);
    }

    public void requestToSignDocuments(UUID statementId) {
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.SEND_SES);
        kafkaEmailService.sendMessage(message);
    }

    public void confirmDocumentSigning(UUID statementId) {
        var message = emailMessageFactory.buildEmailMessage(statementId, EmailTheme.CREDIT_ISSUED);
        kafkaEmailService.sendMessage(message);
    }
}