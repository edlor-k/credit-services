package ru.creditservices.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.dto.*;
import ru.creditservices.calculator.mapper.CreditMapper;
import ru.creditservices.calculator.mapper.ScoringDataMapper;
import ru.creditservices.calculator.model.enums.*;
import ru.creditservices.calculator.service.business.scoring.ScoringCalculator;
import ru.creditservices.calculator.service.business.scoring.ScoringValidator;
import ru.creditservices.calculator.service.business.schedule.PaymentScheduleCalculator;
import ru.creditservices.calculator.service.impl.ScoringService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoringServiceTest {

    private ScoringValidator scoringValidator;
    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringValidator = mock(ScoringValidator.class);
        ScoringCalculator scoringCalculator = mock(ScoringCalculator.class);
        PaymentScheduleCalculator paymentScheduleCalculator = mock(PaymentScheduleCalculator.class);

        CreditMapper creditMapper = mock(CreditMapper.class);
        ScoringDataMapper scoringDataMapper = mock(ScoringDataMapper.class);

        scoringService = new ScoringService(
                scoringValidator,
                scoringCalculator,
                paymentScheduleCalculator,
                scoringDataMapper,
                creditMapper
        );
    }

    @Test
    @DisplayName("Должен бросать ошибку при fail валидации бизнес-логики")
    void shouldThrowWhenBusinessValidationFails() {
        ScoringDataDto badRequest = baseRequestBuilder()
                .amount(BigDecimal.valueOf(10))
                .build();

        doThrow(new RuntimeException("Ошибка!")).when(scoringValidator).validate(any());

        assertThatThrownBy(() -> scoringService.getFinalCreditInfo(badRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка!");
    }

    private ScoringDataDto.ScoringDataDtoBuilder baseRequestBuilder() {
        return ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2010, 1, 1))
                .passportIssueBranch("УФМС России")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employmentINN("1234567890")
                        .salary(BigDecimal.valueOf(120_000))
                        .position(Position.WORKER)
                        .workExperienceTotal(24)
                        .workExperienceCurrent(6)
                        .build())
                .accountNumber("12345678901234567000");
    }
}
