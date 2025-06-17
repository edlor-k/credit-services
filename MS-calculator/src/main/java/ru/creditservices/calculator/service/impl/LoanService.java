package ru.creditservices.calculator.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.mapper.LoanOfferMapper;
import ru.creditservices.calculator.mapper.LoanStatementMapper;
import ru.creditservices.calculator.model.entity.LoanOfferEntity;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;
import ru.creditservices.calculator.service.api.ILoanService;
import ru.creditservices.calculator.service.business.prescoring.LoanOfferCalculator;
import ru.creditservices.calculator.service.business.prescoring.LoanPrescoringValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements ILoanService {

    private final LoanProperties loanProperties;
    private final LoanPrescoringValidator prescoringValidator;
    private final LoanOfferCalculator loanOfferCalculator;

    private final LoanStatementMapper statementMapper;
    private final LoanOfferMapper offerMapper;

    @Override
    public List<LoanOfferDto> getLoanOffers(@Valid LoanStatementRequestDto request) {
        log.info("[LoanService] Start offer calculation. Input: {}", request);

        LoanStatementEntity statement = statementMapper.toEntity(request);

        prescoringValidator.validate(statement);

        List<LoanOfferEntity> offerList = new ArrayList<>(4);
        UUID uuid = UUID.randomUUID();

        BigDecimal baseRate = loanProperties.getBaseRate();
        BigDecimal insuranceRate = loanProperties.getInsuranceRate();
        BigDecimal insuranceCost = loanProperties.getInsuranceCost();
        BigDecimal salaryDiscount = loanProperties.getSalaryDiscount();

        BigDecimal requestedAmount = statement.getAmount();
        Integer term = statement.getTerm();

        log.info("[LoanService] Calculation params: " +
                        "baseRate={}, insuranceRate={}, insuranceCost={}, salaryDiscount={}",
                baseRate, insuranceRate, insuranceCost, salaryDiscount);

        for (boolean isInsuranceEnabled : new boolean[]{true, false}) {
            for (boolean isSalaryClient : new boolean[]{true, false}) {
                log.info("[LoanService] Combination: isInsuranceEnabled={}, isSalaryClient={}",
                        isInsuranceEnabled, isSalaryClient);

                LoanOfferEntity offer = loanOfferCalculator.buildLoanOffer(
                        uuid, requestedAmount, term,
                        baseRate, insuranceRate, insuranceCost, salaryDiscount,
                        isInsuranceEnabled, isSalaryClient
                );
                log.info("[LoanService] Offer: {}", offer);
                offerList.add(offer);
            }
        }

        offerList.sort(Comparator.comparing(LoanOfferEntity::getRate));
        log.info("[LoanService] Final sorted offer list: {}", offerList);

        return offerMapper.toDto(offerList);
    }
}