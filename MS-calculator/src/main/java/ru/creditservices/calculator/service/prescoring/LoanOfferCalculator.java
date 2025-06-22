package ru.creditservices.calculator.service.prescoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.config.LoanProperties;

import static ru.creditservices.calculator.util.CreditCalculationUtil.calculateMonthlyPayment;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanOfferCalculator {

    private final LoanProperties loanProperties;

    public BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        BigDecimal rate = loanProperties.getBaseRate();

        if (isInsuranceEnabled) {
            rate = rate.subtract(loanProperties.getInsuranceRate());
            log.debug("Insurance discount applied. New rate: {}", rate);
        }

        if (isSalaryClient) {
            rate = rate.subtract(loanProperties.getSalaryDiscount());
            log.debug("Salary client discount applied. New rate: {}", rate);
        }

        return rate;
    }

    public BigDecimal calculateTotalAmount(BigDecimal requestedAmount, boolean isInsuranceEnabled) {
        if (isInsuranceEnabled) {
            return requestedAmount.add(loanProperties.getInsuranceCost());
        }
        return requestedAmount;
    }

    public BigDecimal getMonthlyPayment(BigDecimal totalAmount, int term, BigDecimal rate) {
        return calculateMonthlyPayment(totalAmount, term, rate);
    }
}
