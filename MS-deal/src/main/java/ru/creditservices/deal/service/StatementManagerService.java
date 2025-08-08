package ru.creditservices.deal.service;

import ru.creditservices.deal.model.entity.*;

import java.util.List;
import java.util.UUID;

public interface StatementManagerService {
    StatementEntity createStatementFromClient(ClientEntity client);
    void selectLoanOfferToStatement(LoanOfferEntity loanOfferEntity);
    void updateStatementFromScoringData(ScoringDataEntity scoringDataEntity, UUID statementId);
    void addCreditToStatement(UUID statementId, CreditEntity creditEntity);
    void setLoanWaiver(UUID uuid);
    StatementEntity getStatementOrThrow(UUID uuid);
    List<StatementEntity> getAllStatements();
    String generateSesCode(UUID statementId);
    void prepareDocuments(UUID statementId);
    void documentsSigned(UUID statementId);
    void updateStatementStatus(UUID statementId, String status);
}
