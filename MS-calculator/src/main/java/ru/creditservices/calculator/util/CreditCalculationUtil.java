package ru.creditservices.calculator.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public final class CreditCalculationUtil {
    
    private CreditCalculationUtil() {}

    public static BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, int term, BigDecimal annualRate) {
        BigDecimal monthlyRate = toMonthlyRate(annualRate);
        BigDecimal numerator = totalAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(monthlyRate).pow(-term, MathContext.DECIMAL128)
        );
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal toMonthlyRate(BigDecimal annualRate) {
        return annualRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
    }

}