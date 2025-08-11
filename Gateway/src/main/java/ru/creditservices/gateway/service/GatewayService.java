package ru.creditservices.gateway.service;

import ru.creditservices.gateway.dto.FinishRegistrationRequestDto;
import ru.creditservices.gateway.dto.LoanOfferDto;
import ru.creditservices.gateway.dto.LoanStatementRequestDto;
import ru.creditservices.gateway.dto.StatementDto;

import java.util.List;
import java.util.UUID;

public interface GatewayService {
    List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto request);
    void selectLoanOffer(LoanOfferDto loanOfferDto);
    void finishRegistration(FinishRegistrationRequestDto request, UUID statementId);
    void createDocuments(UUID statementId);
    void signDocuments(UUID statementId);
    void verifyDocuments(UUID statementId, String sesCode);
    List<StatementDto> fetchStatements();
    StatementDto fetchStatementById(UUID statementId);
}
