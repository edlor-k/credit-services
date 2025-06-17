package ru.creditservices.calculator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.dto.ScoringDataDto;
import ru.creditservices.calculator.mapper.CreditMapper;
import ru.creditservices.calculator.mapper.ScoringDataMapper;
import ru.creditservices.calculator.model.entity.CreditEntity;
import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.service.api.IScoringService;
import ru.creditservices.calculator.service.business.scoring.ScoringCalculator;
import ru.creditservices.calculator.service.business.scoring.ScoringValidator;
import ru.creditservices.calculator.service.business.schedule.PaymentScheduleCalculator;
import ru.creditservices.calculator.util.PskUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoringService implements IScoringService {

    private final ScoringValidator scoringValidator;
    private final ScoringCalculator scoringCalculator;
    private final PaymentScheduleCalculator paymentScheduleCalculator;

    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;

    @Override
    public CreditDto getFinalCreditInfo(ScoringDataDto data) {
        log.info("[ScoringService] Credit calculation request: {}", data);

        ScoringDataEntity scoringData = scoringDataMapper.toEntity(data);

        try {
            scoringValidator.validate(scoringData);
            log.info("[ScoringService] Business validation: PASSED");
        } catch (Exception e) {
            log.warn("[ScoringService] Business validation: FAILED. Reason: {}", e.getMessage());
            throw e;
        }

        BigDecimal finalRate = scoringCalculator.calculateFinalRate(scoringData);
        log.info("[ScoringService] Calculated final rate: {}", finalRate);

        BigDecimal totalAmount = scoringData.getAmount();
        if (Boolean.TRUE.equals(scoringData.getIsInsuranceEnabled())) {
            totalAmount = totalAmount.add(scoringCalculator.getInsuranceCost());
            log.info("[ScoringService] Insurance enabled: totalAmount increased to {}", totalAmount);
        }

        int term = scoringData.getTerm();
        BigDecimal monthlyRate = finalRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal numerator = totalAmount.multiply(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(monthlyRate).pow(-term, java.math.MathContext.DECIMAL128)
        );
        BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        log.info("[ScoringService] Calculated monthly payment: {}", monthlyPayment);

        LocalDate issueDate = LocalDate.now();
        List<PaymentScheduleElementEntity> schedule = paymentScheduleCalculator.calculateSchedule(
                totalAmount, term, finalRate, issueDate, monthlyPayment
        );
        log.info("[ScoringService] Payment schedule generated ({} entries)", schedule.size());

        BigDecimal psk = PskUtil.calculatePSK(schedule, scoringData.getAmount(), issueDate);
        log.info("[ScoringService] Calculated PSK: {}", psk);

        CreditEntity creditEntity = CreditEntity.builder()
                .amount(scoringData.getAmount())
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(finalRate)
                .psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(schedule)
                .build();

        log.info("[ScoringService] Credit calculation result: {}", creditEntity);

        return creditMapper.toDto(creditEntity);
    }
}
