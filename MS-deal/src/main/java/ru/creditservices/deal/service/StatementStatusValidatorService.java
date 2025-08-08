package ru.creditservices.deal.service;

import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.model.enums.ApplicationStatus;

import java.util.List;

public interface StatementStatusValidatorService {
    void validateStatus(StatementEntity statement, List<ApplicationStatus> allowed,
                        String actionDescription);
}
