package ru.creditservices.deal.service;

import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.LoanStatementEntity;
import ru.creditservices.deal.model.entity.ScoringDataEntity;

public interface ClientManagerService {
    ClientEntity createClientFromLoanStatementRequest(LoanStatementEntity entity);
    void updateClientInformationFromScoringData(ScoringDataEntity scoringDataEntity);
}
