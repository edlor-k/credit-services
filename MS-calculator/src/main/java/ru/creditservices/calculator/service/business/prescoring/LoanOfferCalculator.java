package ru.creditservices.calculator.service.business.prescoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.dto.LoanOfferDto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Component
public class LoanOfferCalculator {

    public LoanOfferDto buildLoanOffer(
            UUID uuid,
            BigDecimal requestedAmount,
            Integer term,
            BigDecimal baseRate,
            BigDecimal insuranceRate,
            BigDecimal insuranceCost,
            BigDecimal salaryDiscount,
            boolean isInsuranceEnabled,
            boolean isSalaryClient
    ) {
        log.info("[LoanOfferCalculator] Start calculation for: " +
                        "isInsuranceEnabled={}, isSalaryClient={}, requestedAmount={}, term={}",
                isInsuranceEnabled, isSalaryClient, requestedAmount, term);

        BigDecimal rate = baseRate;
        BigDecimal totalAmount = requestedAmount;

        if (isInsuranceEnabled) {
            rate = rate.subtract(insuranceRate);
            totalAmount = totalAmount.add(insuranceCost);
            log.info("[LoanOfferCalculator] Insurance enabled. Rate now: {}, totalAmount: {}", rate, totalAmount);
        }
        if (isSalaryClient) {
            rate = rate.subtract(salaryDiscount);
            log.info("[LoanOfferCalculator] Salary client. Rate now: {}", rate);
        }

        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal numerator = totalAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(monthlyRate).pow(-term, MathContext.DECIMAL128)
        );
        BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        log.info("[LoanOfferCalculator] Result: rate={}, totalAmount={}, monthlyPayment={}",
                rate, totalAmount, monthlyPayment);

        LoanOfferDto offer = LoanOfferDto.builder()
                .statementId(uuid)
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

        log.info("[LoanOfferCalculator] Built LoanOfferDto: {}", offer);

        return offer;
    }
}
