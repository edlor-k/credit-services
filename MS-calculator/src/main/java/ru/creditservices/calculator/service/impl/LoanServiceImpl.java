package ru.creditservices.calculator.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.mapper.LoanOfferMapper;
import ru.creditservices.calculator.mapper.LoanStatementMapper;
import ru.creditservices.calculator.model.entity.LoanOfferEntity;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;
import ru.creditservices.calculator.service.LoanService;
import ru.creditservices.calculator.service.prescoring.LoanOfferCalculator;
import ru.creditservices.calculator.service.prescoring.LoanPrescoringValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanPrescoringValidator prescoringValidator;
    private final LoanOfferCalculator loanOfferCalculator;

    private final LoanStatementMapper statementMapper;
    private final LoanOfferMapper offerMapper;

    @Override
    public List<LoanOfferDto> getLoanOffers(@Valid LoanStatementRequestDto request) {
        log.info("Start offer calculation. Input: {}", request);

        LoanStatementEntity statement = statementMapper.toEntity(request);
        prescoringValidator.validate(statement);

        List<LoanOfferEntity> offerList = generateLoanOffers(statement.getAmount(), statement.getTerm());

        offerList.sort(Comparator.comparing(LoanOfferEntity::getRate));
        log.info("Final sorted offer list: {}", offerList);

        return offerMapper.toDto(offerList);
    }

    private List<LoanOfferEntity> generateLoanOffers(BigDecimal requestedAmount, Integer term) {
        List<LoanOfferEntity> offers = new ArrayList<>(4);

        for (boolean isInsuranceEnabled : new boolean[]{true, false}) {
            for (boolean isSalaryClient : new boolean[]{true, false}) {
                log.info("Combination: isInsuranceEnabled={}, isSalaryClient={}",
                        isInsuranceEnabled, isSalaryClient);

                LoanOfferEntity offer = buildLoanOffer(requestedAmount, term,
                        isInsuranceEnabled, isSalaryClient);
                log.info("Offer: {}", offer);
                offers.add(offer);
            }
        }

        return offers;
    }

    private LoanOfferEntity buildLoanOffer(BigDecimal requestedAmount,
                                           Integer term,
                                           boolean isInsuranceEnabled,
                                           boolean isSalaryClient) {

        BigDecimal rate = loanOfferCalculator.calculateRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = loanOfferCalculator.calculateTotalAmount(requestedAmount, isInsuranceEnabled);
        BigDecimal monthlyPayment = loanOfferCalculator.getMonthlyPayment(totalAmount, term, rate);

        return LoanOfferEntity.builder()
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

}