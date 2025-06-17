package ru.creditservices.calculator.util;

import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PskUtil {
    public static BigDecimal calculatePSK(List<PaymentScheduleElementEntity> schedule,
                                    BigDecimal amountIssued,
                                    LocalDate startDate) {
        double lower = 0.0001;
        double upper = 1.0;
        double eps = 1e-7;
        double r = 0.0;
        int iter = 0;

        while ((upper - lower) > eps && iter < 10000) {
            iter++;
            r = (lower + upper) / 2.0;
            double npv = 0.0;
            for (PaymentScheduleElementEntity payment : schedule) {
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

        return BigDecimal.valueOf(r * 100).setScale(3, RoundingMode.HALF_UP);
    }
}
