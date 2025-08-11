package ru.creditservices.deal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.exception.InvalidApplicationStatus;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.service.StatementStatusValidatorService;

import java.util.List;

@Service
@Slf4j
public class StatementStatusValidatorServiceImpl implements StatementStatusValidatorService {
    @Override
    public void validateStatus(StatementEntity statement, List<ApplicationStatus> allowed,
                               String actionDescription) {
        if (!allowed.contains(statement.getStatus())) {
            log.error("Invalid status for statementId={}, expected one of {}, current status is {}",
                    statement.getStatementId(), allowed, statement.getStatus());
            throw new InvalidApplicationStatus("Cannot " + actionDescription + " for statementId="
                    + statement.getStatementId() + ": current status is " + statement.getStatus());
        }
    }
}
