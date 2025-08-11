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
import ru.creditservices.deal.repository.StatementRepository;
import ru.creditservices.deal.service.StatementManagerService;
import ru.creditservices.deal.service.StatementStatusValidatorService;
import ru.creditservices.deal.service.StatusHistoryService;
import ru.creditservices.deal.util.SesCodeGeneratorUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementManagerServiceImpl implements StatementManagerService {

    private final StatementRepository statementRepository;
    private final StatusHistoryService statusHistoryService;
    private final StatementStatusValidatorService statementStatusValidatorService;

    @Override
    @Transactional
    public StatementEntity createStatementFromClient(ClientEntity client) {
        boolean exists = statementRepository.findStatementEntityByClient(client).isPresent();
        if (exists) {
            log.warn("Statement already exists for clientId={}", client.getClientId());
            throw new StatementAlreadyExistException(
                    "Statement for client " + client.getClientId() + " already exists");
        }
        StatementEntity statement = statementRepository.save(buildInitialStatement(client));
        log.info("Statement created: statementId={}, clientId={}", statement.getStatementId(), client.getClientId());
        return statement;
    }

    @Override
    @Transactional
    public void selectLoanOfferToStatement(LoanOfferEntity loanOfferEntity) {
        StatementEntity updatedStatement = getStatementOrThrow(loanOfferEntity.getStatementId());
        statementStatusValidatorService.validateStatus(updatedStatement, List.of(ApplicationStatus.PREAPPROVAL),
                "select loan offer");

        if (updatedStatement.getAppliedOffer() != null) {
            log.error("Loan offer already selected for statementId={}", updatedStatement.getStatementId());
            throw new LoanOfferAlreadyExist("Loan offer already selected for statementId="
                    + updatedStatement.getStatementId());
        }

        updateStatementStatus(updatedStatement, ApplicationStatus.APPROVED);
        updatedStatement.setAppliedOffer(loanOfferEntity);

        statementRepository.save(updatedStatement);
        log.info("Loan offer attached to statementId={}", updatedStatement.getStatementId());
    }

    @Override
    @Transactional
    public void updateStatementFromScoringData(ScoringDataEntity scoringDataEntity, UUID statementId) {
        StatementEntity updatedStatement = getStatementOrThrow(statementId);
        statementStatusValidatorService.validateStatus(updatedStatement,
                List.of(ApplicationStatus.APPROVED, ApplicationStatus.CC_DENIED),
                "update from scoring data");

        updateStatementStatus(updatedStatement, ApplicationStatus.CC_APPROVED);
        statementRepository.save(updatedStatement);
        log.info("Statement status updated to CC_APPROVED: statementId={}", statementId);
    }

    @Override
    @Transactional
    public void addCreditToStatement(UUID statementId, CreditEntity creditEntity) {
        StatementEntity updatedStatement = getStatementOrThrow(statementId);
        updatedStatement.setCredit(creditEntity);
        statementRepository.save(updatedStatement);
        log.info("Credit added to statement: statementId={}, creditId={}", statementId, creditEntity.getCreditId());
    }

    @Override
    @Transactional
    public void setLoanWaiver(UUID uuid) {
        StatementEntity updatedStatement = getStatementOrThrow(uuid);
        updateStatementStatus(updatedStatement, ApplicationStatus.CC_DENIED);
        statementRepository.save(updatedStatement);
        log.warn("Loan waiver set for statementId={}", uuid);
    }

    @Override
    @Transactional
    public StatementEntity getStatementOrThrow(UUID statementId) {
        return statementRepository.findStatementEntityByStatementId(statementId)
                .orElseThrow(() -> {
                    log.error("Statement not found: statementId={}", statementId);
                    return new StatementNotFoundException("Statement not found with ID: " + statementId);
                });
    }

    @Override
    @Transactional
    public List<StatementEntity> getAllStatements() {
        return statementRepository.findAll();
    }

    @Transactional
    @Override
    public void prepareDocuments(UUID statementId) {
        StatementEntity statement = getStatementOrThrow(statementId);
        statementStatusValidatorService.validateStatus(statement, List.of(ApplicationStatus.CC_APPROVED),
                "prepare documents");

        updateStatementStatus(statement, ApplicationStatus.PREPARE_DOCUMENTS);
        statementRepository.save(statement);
        log.info("Prepared documents for statementId={}", statementId);
    }

    @Transactional
    @Override
    public String generateSesCode(UUID statementId) {
        StatementEntity statement = getStatementOrThrow(statementId);
        String sesCode = SesCodeGeneratorUtil.generateSesCode();
        statement.setSesCode(sesCode);
        updateStatementStatus(statement, ApplicationStatus.DOCUMENTS_CREATED);
        statementRepository.save(statement);
        log.info("Generated SES code for statementId={}: {}", statementId, sesCode);
        return sesCode;
    }

    @Transactional
    @Override
    public void documentsSigned(UUID statementId) {
        StatementEntity updatedStatement = getStatementOrThrow(statementId);
        statementStatusValidatorService.validateStatus(updatedStatement, List.of(ApplicationStatus.DOCUMENTS_CREATED),
                "sign documents");

        updateStatementStatus(updatedStatement, ApplicationStatus.DOCUMENT_SIGNED);
        statementRepository.save(updatedStatement);
        log.info("Documents signed for statementId={}", statementId);
    }

    @Transactional
    @Override
    public void updateStatementStatus(UUID statementId, String status) {
        StatementEntity statement = getStatementOrThrow(statementId);
        log.debug("Updating status for statementId: {}, new status: {}", statementId, status);
        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            statementStatusValidatorService.validateStatus(statement, List.of(ApplicationStatus.values()),
                    "update status");
            updateStatementStatus(statement, newStatus);
            statementRepository.save(statement);
            log.info("Statement status updated: statementId={}, new status={}", statementId, newStatus);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid status: {}", status);
            throw new InvalidApplicationStatus("Invalid status: " + status);
        }
    }

    private StatementEntity buildInitialStatement(ClientEntity client) {
        return StatementEntity.builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(statusHistoryService.initialHistory())
                .build();
    }

    private void updateStatementStatus(StatementEntity statement, ApplicationStatus newStatus) {
        statement.setStatus(newStatus);
        statement.setStatusHistory(
                statusHistoryService.addStatus(statement.getStatusHistory(), newStatus));
    }
}
