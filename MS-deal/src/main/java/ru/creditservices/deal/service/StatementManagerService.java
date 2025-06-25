package ru.creditservices.deal.service;

import ru.creditservices.deal.model.entity.*;

import java.util.UUID;

public interface StatementManagerService {
    StatementEntity createStatementFromClient(ClientEntity client);
    void selectLoanOfferToStatement(LoanOfferEntity loanOfferEntity);
    void updateStatementFromScoringData(ScoringDataEntity scoringDataEntity, UUID statementId);
    void addCreditToStatement(UUID statementId, CreditEntity creditEntity);
    void setLoanWaiver(UUID uuid);
    StatementEntity getStatementOrThrow(UUID uuid);
}
