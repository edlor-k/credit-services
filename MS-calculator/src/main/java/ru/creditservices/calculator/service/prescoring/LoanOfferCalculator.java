package ru.creditservices.calculator.service.prescoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.model.entity.LoanOfferEntity;

import static ru.creditservices.calculator.util.CreditCalculationUtil.calculateMonthlyPayment;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanOfferCalculator {

    private final LoanProperties loanProperties;

    public LoanOfferEntity buildLoanOffer(
            BigDecimal requestedAmount,
            Integer term,
            boolean isInsuranceEnabled,
            boolean isSalaryClient
    ) {
        BigDecimal baseRate = loanProperties.getBaseRate();
        BigDecimal insuranceRate = loanProperties.getInsuranceRate();
        BigDecimal insuranceCost = loanProperties.getInsuranceCost();
        BigDecimal salaryDiscount = loanProperties.getSalaryDiscount();

        log.info("Start calculation for: " +
                        "isInsuranceEnabled={}, isSalaryClient={}, requestedAmount={}, term={}",
                isInsuranceEnabled, isSalaryClient, requestedAmount, term);

        BigDecimal rate = baseRate;
        BigDecimal totalAmount = requestedAmount;

        if (isInsuranceEnabled) {
            rate = rate.subtract(insuranceRate);
            totalAmount = totalAmount.add(insuranceCost);
            log.info("Insurance enabled. Rate now: {}, totalAmount: {}", rate, totalAmount);
        }
        if (isSalaryClient) {
            rate = rate.subtract(salaryDiscount);
            log.info("Salary client. Rate now: {}", rate);
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, term, rate);

        log.info("Result: rate={}, totalAmount={}, monthlyPayment={}",
                rate, totalAmount, monthlyPayment);

        LoanOfferEntity offer = LoanOfferEntity.builder()
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

        log.info("Built LoanOfferDto: {}", offer);

        return offer;
    }
}
