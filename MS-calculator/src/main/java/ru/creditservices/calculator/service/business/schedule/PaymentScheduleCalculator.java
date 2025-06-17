package ru.creditservices.calculator.service.business.schedule;

import org.springframework.stereotype.Component;
import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentScheduleCalculator {

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
