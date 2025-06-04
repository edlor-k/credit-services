package ru.creditservices.calculator.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.dto.*;
import ru.creditservices.calculator.model.enums.Gender;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class CalculatorService {

    private final LoanProperties loanProperties;

    public List<LoanOfferDto> prescoring(@Valid LoanStatementRequestDto request) {
        log.info("Prescoring request: {}", request);
        List<LoanOfferDto> offerList = new ArrayList<>(4);
        UUID uuid = UUID.randomUUID();
        BigDecimal baseRate = loanProperties.getBaseRate();
        BigDecimal insuranceRate = loanProperties.getInsuranceRate();
        BigDecimal insuranceCost = loanProperties.getInsuranceCost();
        BigDecimal salaryDiscount = loanProperties.getSalaryDiscount();

        BigDecimal requestedAmount = request.getAmount();
        Integer term = request.getTerm();

        BigDecimal rate;
        BigDecimal totalAmount;
        BigDecimal monthlyPayment;

        for (boolean isInsuranceEnabled : new boolean[]{true, false}) {
            for (boolean isSalaryClient : new boolean[]{true, false}) {
                rate = baseRate;
                totalAmount = requestedAmount;
                if (isInsuranceEnabled) {
                    rate = rate.subtract(insuranceRate);
                    totalAmount = totalAmount.add(insuranceCost);
                }
                if (isSalaryClient) {
                    rate = rate.subtract(salaryDiscount);
                }

                BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
                BigDecimal numerator = totalAmount.multiply(monthlyRate);
                BigDecimal denominator = BigDecimal.ONE.subtract(
                        BigDecimal.ONE.add(monthlyRate).pow(-term, MathContext.DECIMAL128)
                );
                monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);


                offerList.add(LoanOfferDto.builder()
                        .statementId(uuid)
                        .requestedAmount(requestedAmount)
                        .totalAmount(totalAmount)
                        .term(term)
                        .monthlyPayment(monthlyPayment)
                        .rate(rate)
                        .isInsuranceEnabled(isInsuranceEnabled)
                        .isSalaryClient(isSalaryClient)
                        .build());
                log.info("Generated loan offer: with insurance: {}, salary client: {}",
                        isInsuranceEnabled, isSalaryClient);
            }
        }

        offerList.sort(Comparator.comparing(LoanOfferDto::getRate));
        log.info("Generated loan offers: {}", offerList);
        return offerList;
    }

    public CreditDto calculate(@Valid ScoringDataDto scoringData) {
        BigDecimal rate = loanProperties.getBaseRate();
        BigDecimal amount = scoringData.getAmount();
        BigDecimal totalAmount = amount;
        int term = scoringData.getTerm();
        int clientAge = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
        switch(scoringData.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED:
                rate = rate.add(loanProperties.getSelfEmployedIncrease());
                break;
            case BUSINESS_OWNER:
                rate = rate.add(loanProperties.getBusinessOwnerIncrease());
                break;
            default:
                break;
        }
        switch (scoringData.getEmployment().getPosition()) {
            case MIDDLE:
                rate = rate.subtract(loanProperties.getPositionMiddleDiscount());
                break;
            case SENIOR:
                rate = rate.subtract(loanProperties.getPositionTopDiscount());
                break;
            default:
                break;
        }
        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                rate = rate.subtract(loanProperties.getMaritalMarriedDiscount());
                break;
            case DIVORCED:
                rate = rate.add(loanProperties.getMaritalDivorcedIncrease());
                break;
            default:
                break;
        }
        if (scoringData.getGender() == Gender.MALE && clientAge >= 30 && clientAge <= 55 ) {
            rate = rate.subtract(loanProperties.getGenderMaleDiscount());
        } else if (scoringData.getGender() == Gender.FEMALE && clientAge >= 32 && clientAge <= 60) {
            rate = rate.subtract(loanProperties.getGenderFemaleDiscount());
        } else if (scoringData.getGender() == Gender.OTHER) {
            rate = rate.add(loanProperties.getGenderOtherIncrease());
        }
        if (scoringData.getIsInsuranceEnabled()) {
            totalAmount = totalAmount.add(loanProperties.getInsuranceCost());
            rate = rate.subtract(loanProperties.getInsuranceRate());
        }

        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount;
        LocalDate issueDate = LocalDate.now();
        BigDecimal ratePerYear = rate.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal monthlyRate = ratePerYear.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP); // 0.011667

        BigDecimal numerator = totalAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(monthlyRate).pow(-term, MathContext.DECIMAL128)
        );

        BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= term; i++) {
            LocalDate paymentDate = issueDate.plusMonths(i);

            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment)
                    .setScale(2, RoundingMode.HALF_UP);


            remainingDebt = remainingDebt
                    .subtract(debtPayment)
                    .max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            schedule.add(PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(paymentDate)
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt)
                    .build());
        }

        BigDecimal psk = calculatePSK(schedule, scoringData.getAmount(), issueDate);
        log.info("Credit calculation completed. Rate: {}, Monthly Payment: {}, PSK: {}",
                rate, monthlyPayment, psk);
        return CreditDto.builder()
                .amount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(schedule)
                .build();
    }

    private BigDecimal calculatePSK(
            List<PaymentScheduleElementDto> schedule,
            BigDecimal amountIssued,
            LocalDate startDate
    ) {
        double lower = 0.0001;
        double upper = 1.0;
        double eps = 1e-7;
        double r = 0.0;

        while ((upper - lower) > eps) {
            r = (lower + upper) / 2.0;
            double npv = 0.0;
            for (PaymentScheduleElementDto payment : schedule) {
                long days = ChronoUnit.DAYS.between(startDate, payment.getDate());
                double discount = Math.pow(1 + r, days / 365.0);
                npv += payment.getTotalPayment().doubleValue() / discount;
            }
            if (npv > amountIssued.doubleValue()) {
                lower = r;
            } else {
                upper = r;
            }
        }

        return BigDecimal.valueOf(r * 100).setScale(2, RoundingMode.HALF_UP);
    }
}