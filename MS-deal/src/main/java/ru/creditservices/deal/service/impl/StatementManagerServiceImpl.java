package ru.creditservices.deal.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.exception.InvalidApplicationStatus;
import ru.creditservices.deal.exception.LoanOfferAlreadyExist;
import ru.creditservices.deal.exception.StatementAlreadyExistException;
import ru.creditservices.deal.exception.StatementNotFoundException;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.model.enums.ApplicationStatus;
import ru.creditservices.deal.model.jsonb.StatusHistoryElement;
import ru.creditservices.deal.repository.StatementRepository;
import ru.creditservices.deal.service.StatementManagerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementManagerServiceImpl implements StatementManagerService {

    private final StatementRepository statementRepository;

    @Override
    @Transactional
    public StatementEntity createStatementFromClient(ClientEntity client) {
        log.info("Creating initial statement for client: {}", client);
        boolean exists = statementRepository.findStatementEntityByClient(client).isPresent();

        if (exists) {
            log.warn("Statement for client {} already exists", client.getClientId());
            throw new StatementAlreadyExistException(
                    "Statement for client " + client.getClientId() + " already exists");
        }
        log.info("Creating initial statement for client: {}", client);
        return statementRepository.save(buildInitialStatement(client));
    }

    @Override
    @Transactional
    public void selectLoanOfferToStatement(LoanOfferEntity loanOfferEntity) {
        log.info("Selecting loan offer with ID: {} for statement", loanOfferEntity.getStatementId());
        StatementEntity updatedStatement = getStatementOrThrow(loanOfferEntity.getStatementId());

        checkStatementStatus(updatedStatement, List.of(ApplicationStatus.PREAPPROVAL),
                "select loan offer");

        if (updatedStatement.getAppliedOffer() != null) {
            log.error("Loan offer already selected for statement with ID {}", updatedStatement.getStatementId());
            throw new LoanOfferAlreadyExist("Loan offer already selected for statement with ID "
                    + updatedStatement.getStatementId());
        }

        updatedStatement.setStatus(ApplicationStatus.APPROVED);
        addStatusToHistory(updatedStatement, ApplicationStatus.APPROVED);
        updatedStatement.setAppliedOffer(loanOfferEntity);

        statementRepository.save(updatedStatement);
    }

    @Override
    @Transactional
    public void updateStatementFromScoringData(ScoringDataEntity scoringDataEntity, UUID statementId) {
        log.info("Updating statement with ID: {} from scoring data", statementId);
        StatementEntity updatedStatement = getStatementOrThrow(statementId);

        checkStatementStatus(updatedStatement, List.of(ApplicationStatus.APPROVED, ApplicationStatus.CC_DENIED),
                "update from scoring data");

        updatedStatement.setStatus(ApplicationStatus.CC_APPROVED);
        addStatusToHistory(updatedStatement, ApplicationStatus.CC_APPROVED);
        statementRepository.save(updatedStatement);
    }

    @Override
    @Transactional
    public void addCreditToStatement(UUID statementId, CreditEntity creditEntity) {
        log.info("Adding credit to statement with ID: {}", statementId);
        StatementEntity updatedStatement = getStatementOrThrow(statementId);
        updatedStatement.setCredit(creditEntity);
        statementRepository.save(updatedStatement);
    }

    @Override
    @Transactional
    public void setLoanWaiver(UUID uuid) {
        log.warn("Loan waiver for statement with ID {} set", uuid);
        StatementEntity updatedStatement = getStatementOrThrow(uuid);
        updatedStatement.setStatus(ApplicationStatus.CC_DENIED);
        addStatusToHistory(updatedStatement, ApplicationStatus.CC_DENIED);
        statementRepository.save(updatedStatement);
    }

    @Override
    @Transactional
    public StatementEntity getStatementOrThrow(UUID statementId) {
        return statementRepository.findStatementEntityByStatementId(statementId)
                .orElseThrow(() -> {
                    log.error("Statement not found with ID: {}", statementId);
                    return new StatementNotFoundException("Statement not found with ID: " + statementId);
                });
    }

    private void addStatusToHistory(StatementEntity statementEntity, ApplicationStatus status) {
        List<StatusHistoryElement> statusHistory = new ArrayList<>(statementEntity.getStatusHistory());
        statusHistory.add(StatusHistoryElement.builder()
                .status(status)
                .time(LocalDateTime.now())
                .build());
        statementEntity.setStatusHistory(statusHistory);
    }

    private StatementEntity buildInitialStatement(ClientEntity client) {
        return StatementEntity.builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(initialHistoryList())
                .build();
    }

    private List<StatusHistoryElement> initialHistoryList() {
        return List.of(StatusHistoryElement.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .build());
    }

    private void checkStatementStatus(StatementEntity statement,
                                      List<ApplicationStatus> expected, String actionDescription) {
        if (!expected.contains(statement.getStatus())) {
            log.error("Cannot {} for statement with ID {}: current status is {}",
                    actionDescription, statement.getStatementId(), statement.getStatus());
            throw new InvalidApplicationStatus("Cannot " + actionDescription + " for statement with ID "
                    + statement.getStatementId() + ": current status is " + statement.getStatus());
        }
    }

}