package ru.creditservices.statement.service;

import ru.creditservices.statement.model.entity.LoanStatementEntity;

public interface PrescoringService {
    void businessValidate(LoanStatementEntity entity);
}
