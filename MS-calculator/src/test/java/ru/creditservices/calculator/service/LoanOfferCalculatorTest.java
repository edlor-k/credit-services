package ru.creditservices.calculator.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.service.prescoring.LoanOfferCalculator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanOfferCalculatorTest {

    @Test
    @DisplayName("Расчет ставки со страховкой и зарплатным клиентом")
    void testCalculateRateWithInsuranceAndSalary() {
        LoanProperties loanProperties = new LoanProperties();
        loanProperties.setBaseRate(new BigDecimal("16.0"));
        loanProperties.setInsuranceRate(new BigDecimal("3.0"));
        loanProperties.setSalaryDiscount(new BigDecimal("2.0"));
        LoanOfferCalculator calculator = new LoanOfferCalculator(loanProperties);
        BigDecimal rate = calculator.calculateRate(true, true);
        assertEquals(new BigDecimal("11.0"), rate);
    }

    @Test
    @DisplayName("Расчет ставки без страховки и зарплатного клиента")
    void testCalculateRateWithoutInsuranceAndSalary() {
        LoanProperties loanProperties = new LoanProperties();
        loanProperties.setBaseRate(new BigDecimal("16.0"));
        LoanOfferCalculator calculator = new LoanOfferCalculator(loanProperties);
        BigDecimal rate = calculator.calculateRate(false, false);
        assertEquals(new BigDecimal("16.0"), rate);
    }

    @Test
    @DisplayName("Расчет суммы платежа с учетом страховки")
    void testCalculateAmountWithInsurance() {
        LoanProperties loanProperties = new LoanProperties();
        loanProperties.setInsuranceCost(new BigDecimal("50000.0"));
        BigDecimal requestedAmount = new BigDecimal("500000.0");
        LoanOfferCalculator calculator = new LoanOfferCalculator(loanProperties);
        BigDecimal totalAmount = calculator.calculateTotalAmount(requestedAmount, true);
        assertEquals(new BigDecimal("550000.0"), totalAmount);
    }

    @Test
    @DisplayName("Расчет суммы платежа без учета страховки")
    void testCalculateAmountWithoutInsurance() {
        LoanProperties loanProperties = new LoanProperties();
        loanProperties.setInsuranceCost(new BigDecimal("50000.0"));
        BigDecimal requestedAmount = new BigDecimal("500000.0");
        LoanOfferCalculator calculator = new LoanOfferCalculator(loanProperties);
        BigDecimal totalAmount = calculator.calculateTotalAmount(requestedAmount, false);
        assertEquals(new BigDecimal("500000.0"), totalAmount);
    }

    @Test
    @DisplayName("Расчет ежемесячного платежа")
    void testCalculateMonthlyPayment() {
        LoanProperties loanProperties = new LoanProperties();
        loanProperties.setBaseRate(new BigDecimal("16.0"));
        LoanOfferCalculator calculator = new LoanOfferCalculator(loanProperties);
        BigDecimal requestedAmount = new BigDecimal("500000.0");
        int term = 36;
        BigDecimal rate = calculator.calculateRate(false, false);
        BigDecimal totalAmount = calculator.calculateTotalAmount(requestedAmount, false);
        BigDecimal monthlyPayment = calculator.getMonthlyPayment(totalAmount, term, rate);
        assertEquals(new BigDecimal("17578.52"), monthlyPayment);
    }
}
