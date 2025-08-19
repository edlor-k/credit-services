package ru.creditservices.deal.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.mapper.CreditMapper;
import ru.creditservices.deal.mapper.FinishRegistrationMapper;
import ru.creditservices.deal.mapper.ScoringDataMapper;
import ru.creditservices.deal.model.entity.CreditEntity;
import ru.creditservices.deal.model.entity.FinishRegistrationEntity;
import ru.creditservices.deal.model.entity.ScoringDataEntity;
import ru.creditservices.deal.model.entity.StatementEntity;
import ru.creditservices.deal.exception.CreditAlreadyExistException;
import ru.creditservices.deal.service.*;

import java.util.Optional;
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
    public void calculateFinalParameters(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        log.info("Calculate final parameters for statement {}", statementId);

        FinishRegistrationEntity finishRegistrationEntity =
                finishRegistrationMapper.toEntity(finishRegistrationRequestDto);
        StatementEntity statementEntity = getAndCheckStatement(statementId);

        ScoringDataEntity scoringDataEntity = scoringDataAssembler
                .assembleScoringDataEntity(statementEntity, finishRegistrationEntity);

        CreditDto creditDto = calculatorClientService.fetchCalculatorResult(
                scoringDataMapper.toDto(scoringDataEntity));

        handleApproved(creditDto, statementId, scoringDataEntity);
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

    private void handleApproved(CreditDto creditDto, UUID statementId, ScoringDataEntity scoringDataEntity) {
        log.info("Loan approved for statement {}", statementId);
        CreditEntity creditEntity = creditMapper.toEntity(creditDto);
        creditManagerService.createCreditFromCreditEntity(creditEntity);
        statementManagerService.updateStatementFromScoringData(scoringDataEntity, statementId);
        statementManagerService.addCreditToStatement(statementId, creditEntity);
        clientManagerService.updateClientInformationFromScoringData(scoringDataEntity);
        log.debug("Final parameters calculated for statement {}", statementId);
    }
}
