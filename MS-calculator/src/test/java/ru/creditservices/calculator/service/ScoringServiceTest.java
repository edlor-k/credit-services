package ru.creditservices.calculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.dto.EmploymentDto;
import ru.creditservices.calculator.dto.ScoringDataDto;
import ru.creditservices.calculator.exception.ScoringException;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Gender;
import ru.creditservices.calculator.model.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ScoringServiceTest {
    @Autowired
    private ScoringService scoringService;

    private ScoringDataDto validData() {
        return ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(100_000))
                .term(24)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(30))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.now().minusYears(10))
                .passportIssueBranch("ОВД")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .employment(
                        EmploymentDto.builder()
                                .employmentStatus(EmploymentStatus.EMPLOYED)
                                .employmentINN("1234567890")
                                .salary(BigDecimal.valueOf(40_000))
                                .position(null)
                                .workExperienceTotal(24)
                                .workExperienceCurrent(6)
                                .build()
                )
                .accountNumber("12345678901234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    @Test
    void validRequestShouldReturnCreditInfo() {
        ScoringDataDto dto = validData();

        CreditDto credit = scoringService.getFinalCreditInfo(dto);

        assertNotNull(credit, "CreditDto должен быть не null");
        assertEquals(dto.getAmount(), credit.getAmount(), "Сумма кредита должна совпадать");
        assertEquals(dto.getTerm(), credit.getTerm(), "Срок кредита должен совпадать");
        assertTrue(credit.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0, "Платёж должен быть положительным");
        assertNotNull(credit.getRate(), "Ставка должна быть заполнена");
        assertNotNull(credit.getPsk(), "ПСК должен быть заполнен");
        assertEquals(dto.getIsInsuranceEnabled(), credit.getIsInsuranceEnabled(), "Страховка должна совпадать");
        assertEquals(dto.getIsSalaryClient(), credit.getIsSalaryClient(), "Зарплатный клиент должен совпадать");
        assertNotNull(credit.getPaymentSchedule(), "График платежей должен быть заполнен");
        assertFalse(credit.getPaymentSchedule().isEmpty(), "График платежей не должен быть пустым");
    }

    @Test
    void nonBinaryRateShouldBeHigherThanMaleAndFemale() {
        ScoringDataDto male = validData();
        male.setGender(Gender.MALE);
        male.setBirthdate(LocalDate.now().minusYears(30));

        ScoringDataDto female = validData();
        female.setGender(Gender.FEMALE);
        female.setBirthdate(LocalDate.now().minusYears(35));

        ScoringDataDto nonBinary = validData();
        nonBinary.setGender(Gender.NON_BINARY);
        nonBinary.setBirthdate(LocalDate.now().minusYears(30));

        BigDecimal rateMale = scoringService.getFinalCreditInfo(male).getRate();
        BigDecimal rateFemale = scoringService.getFinalCreditInfo(female).getRate();
        BigDecimal rateNonBinary = scoringService.getFinalCreditInfo(nonBinary).getRate();

        assertTrue(rateNonBinary.compareTo(rateMale) > 0);
        assertTrue(rateNonBinary.compareTo(rateFemale) > 0);

        assertEquals(rateMale, rateFemale);
    }


    @Test
    void shouldThrowIfBirthdateNull() {
        ScoringDataDto data = validData();
        data.setBirthdate(null);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("Дата рождения клиента должна быть указана"));
    }

    @Test
    void shouldThrowIfAgeTooYoung() {
        ScoringDataDto data = validData();
        data.setBirthdate(LocalDate.now().minusYears(18));
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("возраст заемщика должен быть от 20 до 65 лет"));
    }

    @Test
    void shouldThrowIfEmploymentNull() {
        ScoringDataDto data = validData();
        data.setEmployment(null);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("Информация о трудоустройстве должна быть указана"));
    }

    @Test
    void shouldThrowIfWorkExperienceTotalTooSmall() {
        ScoringDataDto data = validData();
        data.getEmployment().setWorkExperienceTotal(10);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("общий стаж работы должен быть не менее 18 месяцев"));
    }

    @Test
    void shouldThrowIfWorkExperienceCurrentTooSmall() {
        ScoringDataDto data = validData();
        data.getEmployment().setWorkExperienceCurrent(1);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("стаж на текущем месте работы должен быть не менее 3 месяцев"));
    }

    @Test
    void shouldThrowIfUnemployed() {
        ScoringDataDto data = validData();
        data.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("безработным клиентам кредит не выдается"));
    }

    @Test
    void shouldThrowIfSalaryNull() {
        ScoringDataDto data = validData();
        data.getEmployment().setSalary(null);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("Зарплата должна быть указана"));
    }

    @Test
    void shouldThrowIfAmountNull() {
        ScoringDataDto data = validData();
        data.setAmount(null);
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("Сумма кредита должна быть указана"));
    }

    @Test
    void shouldThrowIfAmountTooLargeForSalary() {
        ScoringDataDto data = validData();
        data.setAmount(BigDecimal.valueOf(1_000_000));
        ScoringException e = assertThrows(ScoringException.class, () ->
                scoringService.getFinalCreditInfo(data));
        assertTrue(e.getMessage().contains("сумма кредита не должна превышать 24 зарплаты"));
    }
}
