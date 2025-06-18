package ru.creditservices.calculator.service.scoring;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.model.enums.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScoringCalculator {

    private final LoanProperties loanProperties;

    public BigDecimal calculateFinalRate(ScoringDataEntity data) {
        BigDecimal rate = loanProperties.getBaseRate();
        rate = adjustRateForEmployment(rate, data);
        rate = adjustRateForMaritalStatus(rate, data);
        rate = adjustRateForGenderAndAge(rate, data);
        rate = adjustRateForInsurance(rate, data);
        rate = adjustRateForSalaryClient(rate, data);
        return rate;
    }

    private BigDecimal adjustRateForEmployment(BigDecimal rate, ScoringDataEntity data) {
        if (data.getEmployment() == null) return rate;
        switch (data.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED -> rate = rate.add(loanProperties.getSelfEmployedIncrease());
            case BUSINESS_OWNER -> rate = rate.add(loanProperties.getBusinessOwnerIncrease());
            default -> {}
        }
        if (data.getEmployment().getPosition() != null) {
            switch (data.getEmployment().getPosition()) {
                case MID_MANAGER -> rate = rate.subtract(loanProperties.getPositionMiddleDiscount());
                case TOP_MANAGER -> rate = rate.subtract(loanProperties.getPositionTopDiscount());
                default -> {}
            }
        }
        return rate;
    }

    private BigDecimal adjustRateForMaritalStatus(BigDecimal rate, ScoringDataEntity data) {
        if (data.getMaritalStatus() == null) return rate;
        switch (data.getMaritalStatus()) {
            case MARRIED -> rate = rate.subtract(loanProperties.getMaritalMarriedDiscount());
            case DIVORCED -> rate = rate.add(loanProperties.getMaritalDivorcedIncrease());
            default -> {}
        }
        return rate;
    }

    private BigDecimal adjustRateForGenderAndAge(BigDecimal rate, ScoringDataEntity data) {
        if (data.getBirthdate() == null || data.getGender() == null) return rate;
        int age = Period.between(data.getBirthdate(), LocalDate.now()).getYears();
        if (data.getGender() == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(loanProperties.getGenderMaleDiscount());
        } else if (data.getGender() == Gender.FEMALE && age >= 32 && age <= 60) {
            rate = rate.subtract(loanProperties.getGenderFemaleDiscount());
        } else if (data.getGender() == Gender.NON_BINARY) {
            rate = rate.add(loanProperties.getGenderOtherIncrease());
        }
        return rate;
    }

    private BigDecimal adjustRateForInsurance(BigDecimal rate, ScoringDataEntity data) {
        if (Boolean.TRUE.equals(data.getIsInsuranceEnabled())) {
            rate = rate.subtract(loanProperties.getInsuranceRate());
        }
        return rate;
    }

    private BigDecimal adjustRateForSalaryClient(BigDecimal rate, ScoringDataEntity data) {
        if (Boolean.TRUE.equals(data.getIsSalaryClient())) {
            rate = rate.subtract(loanProperties.getSalaryDiscount());
        }
        return rate;
    }

    public BigDecimal calculateTotalAmount(ScoringDataEntity data) {
        BigDecimal amount = data.getAmount();
        if (Boolean.TRUE.equals(data.getIsInsuranceEnabled())) {
            amount = amount.add(loanProperties.getInsuranceCost());
        }
        return amount;
    }

    public BigDecimal calculatePsk(
            List<PaymentScheduleElementEntity> schedule,
            BigDecimal originalAmount,
            LocalDate issueDate
    ) {
        BigDecimal totalPayments = schedule.stream()
                .map(PaymentScheduleElementEntity::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overpayPercent = totalPayments.subtract(originalAmount)
                .divide(originalAmount, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        LocalDate lastPaymentDate = schedule.getLast().getDate();
        double years = ChronoUnit.DAYS.between(issueDate, lastPaymentDate) / 365.0;
        if (years == 0) years = 1.0;

        return overpayPercent
                .divide(BigDecimal.valueOf(years), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }


    public List<PaymentScheduleElementEntity> calculateSchedule(
            BigDecimal totalAmount, int term, BigDecimal rate, LocalDate startDate, BigDecimal monthlyPayment) {

        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        List<PaymentScheduleElementEntity> schedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount;

        for (int i = 1; i <= term; i++) {
            LocalDate paymentDate = startDate.plusMonths(i);
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            remainingDebt = remainingDebt.subtract(debtPayment).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            schedule.add(PaymentScheduleElementEntity.builder()
                    .number(i)
                    .date(paymentDate)
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt)
                    .build());
        }
        return schedule;
    }
}
