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
import java.util.Optional;

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
    public void calculateFinalParameters(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        log.info("Calculate final parameters for statement {}", statementId);

        FinishRegistrationEntity finishRegistrationEntity =
                finishRegistrationMapper.toEntity(finishRegistrationRequestDto);
        StatementEntity statementEntity = getAndCheckStatement(statementId);

        ScoringDataEntity scoringDataEntity = scoringDataAssembler
                .assembleScoringDataEntity(statementEntity, finishRegistrationEntity);

        CalculatorResult calculatorResult = calculatorClientService.fetchCalculatorResult(
                scoringDataMapper.toDto(scoringDataEntity));
        processCalculatorResult(calculatorResult, statementId, scoringDataEntity);
    }

    private StatementEntity getAndCheckStatement(UUID statementId) {
        StatementEntity statementEntity = statementManagerService.getStatementOrThrow(statementId);
        Optional.ofNullable(statementEntity.getCredit())
                .ifPresent(credit -> {
                    log.error("Credit already exists for statement {}", statementId);
                    throw new CreditAlreadyExistException("Credit already exists for statement ID: " + statementId);
                });
        return statementEntity;
    }

    private void processCalculatorResult(CalculatorResult result, UUID statementId,
                                         ScoringDataEntity scoringDataEntity) {
        switch (result.type()) {
            case APPROVED -> handleApproved(result, statementId, scoringDataEntity);
            case BUSINESS_DECLINE -> handleBusinessDecline(result, statementId);
            case REQUEST_ERROR -> handleRequestError(result, statementId);
        }
    }

    private void handleApproved(CalculatorResult result, UUID statementId, ScoringDataEntity scoringDataEntity) {
        log.info("Loan approved for statement {}", statementId);
        CreditEntity creditEntity = creditMapper.toEntity(result.creditDto());
        creditManagerService.createCreditFromCreditEntity(creditEntity);
        statementManagerService.updateStatementFromScoringData(scoringDataEntity, statementId);
        statementManagerService.addCreditToStatement(statementId, creditEntity);
        clientManagerService.updateClientInformationFromScoringData(scoringDataEntity);
        log.debug("Final parameters calculated for statement {}", statementId);
    }

    private void handleBusinessDecline(CalculatorResult result, UUID statementId) {
        log.info("Loan declined for statement ID {}: {}", statementId, result.businessDeclineReason());
        statementManagerService.setLoanWaiver(statementId);
    }

    private void handleRequestError(CalculatorResult result, UUID statementId) {
        log.warn("Validation error for statement ID {}: {}", statementId, result.requestErrorMessage());
        throw new CalculatorValidationException(
                CalculatorErrorType.REQUEST_ERROR,
                result.violations()
        );
    }
}