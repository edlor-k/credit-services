package ru.creditservices.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.exception.ScoringException;
import ru.creditservices.calculator.model.entity.EmploymentEntity;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Gender;
import ru.creditservices.calculator.model.enums.MaritalStatus;
import ru.creditservices.calculator.model.enums.Position;
import ru.creditservices.calculator.service.scoring.ScoringValidator;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ScoringValidatorTest {

    private ScoringDataEntity scoringData;

    @BeforeEach
    void setUp() {
        scoringData = ScoringDataEntity.builder()
                .amount(new BigDecimal("50000.0"))
                .term(12)
                .firstName("Petr")
                .lastName("Petrov")
                .middleName("Petrovich")
                .gender(Gender.MALE)
                .birthdate(LocalDate.parse("1985-05-15"))
                .passportSeries("2222")
                .passportNumber("333333")
                .passportIssueDate(LocalDate.parse("2010-01-01"))
                .passportIssueBranch("OVD")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .employment(
                        EmploymentEntity.builder()
                                .employmentStatus(EmploymentStatus.EMPLOYED)
                                .employmentINN("1234567890")
                                .salary(new BigDecimal("40000.0"))
                                .position(Position.WORKER)
                                .workExperienceTotal(60)
                                .workExperienceCurrent(24)
                                .build()
                )
                .accountNumber("12345678901234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    @Test
    @DisplayName("Корректные данные проходят валидацию")
    void validDataDoesNotThrow() {
        ScoringValidator scoringValidator = new ScoringValidator();
        assertDoesNotThrow(() -> scoringValidator.validate(scoringData));
    }

    @Test
    @DisplayName("Зарплата меньше 24 платежей — ошибка")
    void salaryLessThanTwentyFourPaymentsThrows() {
        scoringData.setAmount(new BigDecimal("999999.0"));
        scoringData.getEmployment().setSalary(new BigDecimal("1000.0"));
        ScoringValidator scoringValidator = new ScoringValidator();

        ScoringException ex = assertThrows(
                ScoringException.class,
                () -> scoringValidator.validate(scoringData)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("сумма"));
    }

    @Test
    @DisplayName("Возраст меньше 20 лет — ошибка")
    void ageLessThanTwentyThrows() {
        scoringData.setBirthdate(LocalDate.now().minusYears(19));
        ScoringValidator scoringValidator = new ScoringValidator();

        ScoringException ex = assertThrows(
                ScoringException.class,
                () -> scoringValidator.validate(scoringData)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("от 20"));
    }

    @Test
    @DisplayName("Общий стаж меньше 18 мес — ошибка")
    void totalWorkExperienceLessThanEighteenThrows() {
        scoringData.getEmployment().setWorkExperienceTotal(17);
        ScoringValidator scoringValidator = new ScoringValidator();

        ScoringException ex = assertThrows(
                ScoringException.class,
                () -> scoringValidator.validate(scoringData)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("стаж"));
    }

    @Test
    @DisplayName("Стаж на текущем месте < 3 мес — ошибка")
    void currentWorkExperienceLessThanThreeThrows() {
        scoringData.getEmployment().setWorkExperienceCurrent(2);
        ScoringValidator scoringValidator = new ScoringValidator();

        ScoringException ex = assertThrows(
                ScoringException.class,
                () -> scoringValidator.validate(scoringData)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("стаж"));
    }

    @Test
    @DisplayName("Безработный — ошибка")
    void unemployedStatusThrows() {
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        ScoringValidator scoringValidator = new ScoringValidator();

        ScoringException ex = assertThrows(
                ScoringException.class,
                () -> scoringValidator.validate(scoringData)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("безработным"));
    }

    @Test
    @DisplayName("Нет зарплаты и даты рождения — ошибка")
    void missingSalaryAndBirthdateThrows() {
        scoringData.setBirthdate(null);
        scoringData.getEmployment().setSalary(null);
        ScoringValidator scoringValidator = new ScoringValidator();

        Exception ex = assertThrows(
                ScoringException.class,
                () -> scoringValidator.validate(scoringData)
        );
        String msg = ex.getMessage().toLowerCase();
        assertTrue(
                msg.contains("зарплата") &&
                msg.contains("дата рождения")
        );
    }
}
