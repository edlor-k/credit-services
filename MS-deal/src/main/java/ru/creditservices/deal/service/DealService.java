package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.StatementDto;

import java.util.UUID;
import java.util.List;

public interface DealService {

    List<LoanOfferDto> createLoanStatement(LoanStatementRequestDto dto);

    void selectLoanOffer(LoanOfferDto loanOfferDto);

    void calculateFinalLoanParameters(UUID statementId, FinishRegistrationRequestDto dto);

    void sendDocuments(UUID statementId);

    void requestToSignDocuments(UUID statementId);

    void confirmDocumentSigning(UUID statementId, String sesCode);

    List<StatementDto> getAllStatements();

    StatementDto getStatementById(UUID statementId);
}
