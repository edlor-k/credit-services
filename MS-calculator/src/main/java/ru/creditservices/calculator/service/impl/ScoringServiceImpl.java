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
import ru.creditservices.calculator.service.scoring.ScoringCalculator;
import ru.creditservices.calculator.service.scoring.ScoringValidator;
import static ru.creditservices.calculator.util.CreditCalculationUtil.calculateMonthlyPayment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoringServiceImpl implements ru.creditservices.calculator.service.ScoringService {

    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;
    private final ScoringCalculator scoringCalculator;
    private final ScoringValidator scoringValidator;

    @Override
    public CreditDto getFinalCreditInfo(ScoringDataDto data) {
        log.info("Credit calculation request: {}", data);

        ScoringDataEntity scoringData = scoringDataMapper.toEntity(data);

        int term = scoringData.getTerm();

        scoringValidator.validate(scoringData);
        log.info("Business validation: PASSED");

        BigDecimal finalRate = scoringCalculator.calculateFinalRate(scoringData);
        log.info("Calculated final rate: {}", finalRate);

        BigDecimal totalAmount = scoringCalculator.calculateTotalAmount(scoringData);
        log.info("Calculated total amount: {}", totalAmount);

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, scoringData.getTerm(), finalRate);
        log.info("Calculated monthly payment: {}", monthlyPayment);

        LocalDate issueDate = LocalDate.now();
        List<PaymentScheduleElementEntity> schedule = scoringCalculator.calculateSchedule(
                totalAmount, term, finalRate, issueDate, monthlyPayment
        );
        log.info("Payment schedule generated ({} entries)", schedule.size());

        BigDecimal psk = scoringCalculator.calculatePsk(schedule, scoringData.getAmount(), issueDate);
        log.info("Calculated PSK: {}", psk);

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

        log.info("Credit calculation result: {}", creditEntity);

        return creditMapper.toDto(creditEntity);
    }
}
