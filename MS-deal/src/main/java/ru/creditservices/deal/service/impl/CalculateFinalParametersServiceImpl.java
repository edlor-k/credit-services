package ru.creditservices.deal.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.*;
import ru.creditservices.deal.exception.CalculatorValidationException;
import ru.creditservices.deal.exception.CreditAlreadyExistException;
import ru.creditservices.deal.mapper.CreditMapper;
import ru.creditservices.deal.mapper.FinishRegistrationMapper;
import ru.creditservices.deal.mapper.ScoringDataMapper;
import ru.creditservices.deal.model.entity.CreditEntity;
import ru.creditservices.deal.model.entity.FinishRegistrationEntity;
import ru.creditservices.deal.model.entity.ScoringDataEntity;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.model.enums.CalculatorErrorType;
import ru.creditservices.deal.service.*;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculateFinalParametersServiceImpl implements CalculateFinalParametersService {

    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;
    private final FinishRegistrationMapper finishRegistrationMapper;

    private final StatementManagerService statementManagerService;
    private final CreditManagerService creditManagerService;
    private final ScoringDataAssembler scoringDataAssembler;
    private final ClientManagerService clientManagerService;

    private final CalculatorClientService calculatorClientService;

    @Override
    @Transactional
    public void calculateFinalParameters(
            String statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        log.info("Calculating final parameters for statement ID: {}", statementId);

        FinishRegistrationEntity finishRegistrationEntity =
                finishRegistrationMapper.toEntity(finishRegistrationRequestDto);
        StatementEntity statementEntity = getAndCheckStatement(statementId);

        log.info("Assembling scoring data for statement ID: {}", statementId);
        ScoringDataEntity scoringDataEntity = scoringDataAssembler
                .assembleScoringDataEntity(statementEntity, finishRegistrationEntity);
        log.info("Scoring data assembled for statement ID: {}", statementId);

        log.info("Requesting calculator for final parameters for statement ID: {} with data: {}",
                statementId, scoringDataEntity);
        CalculatorResult calculatorResult = calculatorClientService.fetchCalculatorResult(
                scoringDataMapper.toDto(scoringDataEntity));
        log.info("Received calculator result for statement ID: {}: {}", statementId, calculatorResult);

        processCalculatorResult(calculatorResult, statementId, scoringDataEntity);
    }

    private StatementEntity getAndCheckStatement(String statementId) {
        StatementEntity statementEntity = statementManagerService.getStatementOrThrow(UUID.fromString(statementId));
        if (statementEntity.getCredit() != null) {
            log.error("Credit already exists for statement ID: {}", statementId);
            throw new CreditAlreadyExistException("Credit already exists for statement ID: " + statementId);
        }
        return statementEntity;
    }

    private void processCalculatorResult(
            CalculatorResult result, String statementId, ScoringDataEntity scoringDataEntity) {
        log.info("Processing calculator result for statement ID: {}", statementId);
        switch (result.type()) {
            case APPROVED -> handleApproved(result, statementId, scoringDataEntity);
            case BUSINESS_DECLINE -> handleBusinessDecline(result, statementId);
            case REQUEST_ERROR -> handleRequestError(result, statementId);
        }
    }

    private void handleApproved(CalculatorResult result, String statementId, ScoringDataEntity scoringDataEntity) {
        log.info("Loan approved for statement ID {}: {}", statementId, result.creditDto());
        CreditEntity creditEntity = creditMapper.toEntity(result.creditDto());
        creditManagerService.createCreditFromCreditEntity(creditEntity);
        statementManagerService.updateStatementFromScoringData(scoringDataEntity, UUID.fromString(statementId));
        statementManagerService.addCreditToStatement(UUID.fromString(statementId), creditEntity);
        clientManagerService.updateClientInformationFromScoringData(scoringDataEntity);
        log.info("Final parameters calculated successfully for statement ID: {}", statementId);
    }

    private void handleBusinessDecline(CalculatorResult result, String statementId) {
        log.info("Loan declined for statement ID {}: {}", statementId, result.businessDeclineReason());
        statementManagerService.setLoanWaiver(UUID.fromString(statementId));
    }

    private void handleRequestError(CalculatorResult result, String statementId) {
        log.warn("Validation error for statement ID {}: {}", statementId, result.requestErrorMessage());
        throw new CalculatorValidationException(
                CalculatorErrorType.REQUEST_ERROR,
                result.violations()
        );
    }
}