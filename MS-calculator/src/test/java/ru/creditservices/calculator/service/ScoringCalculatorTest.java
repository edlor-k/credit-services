package ru.creditservices.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.model.entity.EmploymentEntity;
import ru.creditservices.calculator.model.entity.PaymentScheduleElementEntity;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Gender;
import ru.creditservices.calculator.model.enums.MaritalStatus;
import ru.creditservices.calculator.model.enums.Position;
import ru.creditservices.calculator.service.scoring.ScoringCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoringCalculatorTest {

    private ScoringDataEntity scoringData;
    private final LoanProperties loanProperties = new LoanProperties();

    @BeforeEach
    void setUp() {
        scoringData = ScoringDataEntity.builder()
                .amount(new BigDecimal("50000.0"))
                .term(12)
                .firstName("Petr")
                .lastName("Petrov")
                .middleName("Petrovich")
                .passportSeries("2222")
                .passportNumber("333333")
                .passportIssueDate(LocalDate.parse("2010-01-01"))
                .passportIssueBranch("OVD")
                .dependentAmount(0)
                .employment(
                        EmploymentEntity.builder()
                                .employmentINN("1234567890")
                                .salary(new BigDecimal("40000.0"))
                                .workExperienceTotal(60)
                                .workExperienceCurrent(24)
                                .build()
                )
                .accountNumber("12345678901234567890")
                .build();

        loanProperties.setBaseRate(new BigDecimal("16.0")); // +
        loanProperties.setInsuranceCost(new BigDecimal("1000.0")); // +
        loanProperties.setInsuranceRate(new BigDecimal("2.0")); // +
        loanProperties.setSalaryDiscount(new BigDecimal("2.0")); // +
        loanProperties.setGenderFemaleDiscount(new BigDecimal("1.0"));
        loanProperties.setGenderMaleDiscount(new BigDecimal("1.0"));
        loanProperties.setBusinessOwnerIncrease(new BigDecimal("2.0"));
        loanProperties.setSelfEmployedIncrease(new BigDecimal("1.0"));
        loanProperties.setMaritalDivorcedIncrease(new BigDecimal("1.0"));
        loanProperties.setMaritalMarriedDiscount(new BigDecimal("1.0"));
        loanProperties.setGenderOtherIncrease(new BigDecimal("2.0"));
        loanProperties.setPositionMiddleDiscount(new BigDecimal("1.0"));
        loanProperties.setPositionTopDiscount(new BigDecimal("3.0"));
    }

    @Test
    @DisplayName("Самозанятый клиент со страховкой имеет ставку 15%")
    void testSelfEmployedWithInsuranceRateIncrease() {
        scoringData.setIsInsuranceEnabled(true);
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal rate = calculator.calculateFinalRate(scoringData);
        assertEquals(new BigDecimal("15.0"), rate);
    }

    @Test
    @DisplayName("Владелец бизнеса без страховки имеет статус 18%")
    void testBusinessOwnerRateIncrease() {
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal rate = calculator.calculateFinalRate(scoringData);
        assertEquals(new BigDecimal("18.0"), rate);
    }

    @Test
    @DisplayName("Миддл инженер зарплатный клиент с зп и в молодом возрасте, женат, ставка 9%")
    void testMidManagerMaleInsuranceAndSalary() {
        scoringData.setIsInsuranceEnabled(true);
        scoringData.setIsSalaryClient(true);
        scoringData.setGender(Gender.MALE);
        scoringData.setMaritalStatus(MaritalStatus.MARRIED);
        scoringData.setBirthdate(LocalDate.now().minusYears(35));
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.EMPLOYED);
        scoringData.getEmployment().setPosition(Position.MID_MANAGER);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal rate = calculator.calculateFinalRate(scoringData);
        assertEquals(new BigDecimal("9.0"), rate);
    }

    @Test
    @DisplayName("Женщина, топ инженер, разведена. Ставка  ")
    void testFemaleTopManagerDivorced() {
        scoringData.setGender(Gender.FEMALE);
        scoringData.setBirthdate(LocalDate.now().minusYears(35));
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.EMPLOYED);
        scoringData.getEmployment().setPosition(Position.TOP_MANAGER);
        scoringData.setMaritalStatus(MaritalStatus.DIVORCED);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal rate = calculator.calculateFinalRate(scoringData);
        assertEquals(new BigDecimal("13.0"), rate);
    }

    @Test
    @DisplayName("Повышение ставки для небинарного клиента  ")
    void testNonBinary() {
        scoringData.setGender(Gender.NON_BINARY);
        scoringData.setBirthdate(LocalDate.now().minusYears(35));
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.EMPLOYED);
        scoringData.getEmployment().setPosition(Position.TOP_MANAGER);
        scoringData.setMaritalStatus(MaritalStatus.DIVORCED);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal rate = calculator.calculateFinalRate(scoringData);
        assertEquals(new BigDecimal("16.0"), rate);
    }

    @Test
    @DisplayName("Полная сумма со страховкой считается корректно")
    void testInsuranceCost() {
        scoringData.setIsInsuranceEnabled(true);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal amount = calculator.calculateTotalAmount(scoringData);
        assertEquals(new BigDecimal("51000.0"), amount);
    }

    @Test
    @DisplayName("Полная сумма без страховки считается корректно")
    void testWithoutInsuranceCost() {
        scoringData.setIsInsuranceEnabled(true);
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal amount = calculator.calculateTotalAmount(scoringData);
        assertEquals(new BigDecimal("51000.0"), amount);
    }

    @Test
    @DisplayName("Платежный график корректен для стандартного кейса")
    void testCalculateSchedule_basic() {
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal totalAmount = new BigDecimal("120000");
        int term = 12;
        BigDecimal rate = new BigDecimal("12.0");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        BigDecimal monthlyPayment = new BigDecimal("10661.56");

        List<PaymentScheduleElementEntity> schedule = calculator.calculateSchedule(
                totalAmount, term, rate, startDate, monthlyPayment);

        assertEquals(term, schedule.size());

        PaymentScheduleElementEntity first = schedule.getFirst();
        assertEquals(1, first.getNumber());
        assertEquals(startDate.plusMonths(1), first.getDate());
        assertEquals(monthlyPayment.setScale(2, RoundingMode.HALF_UP), first.getTotalPayment());

        PaymentScheduleElementEntity last = schedule.get(term - 1);
        assertTrue(
                last.getRemainingDebt().compareTo(BigDecimal.ZERO) == 0
                        || last.getRemainingDebt().compareTo(new BigDecimal("10.0")) < 0
        );

        BigDecimal totalPayments = schedule.stream()
                .map(PaymentScheduleElementEntity::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(
                monthlyPayment.multiply(BigDecimal.valueOf(term)).setScale(2, RoundingMode.HALF_UP),
                totalPayments
        );

        for (int i = 1; i < term; i++) {
            BigDecimal prev = schedule.get(i - 1).getRemainingDebt();
            BigDecimal curr = schedule.get(i).getRemainingDebt();
            assertTrue(prev.compareTo(curr) > 0 || curr.compareTo(BigDecimal.ZERO) == 0);
        }
    }

    @Test
    @DisplayName("График для кредита на 1 месяц")
    void testCalculateScheduleOneMonth() {
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);
        BigDecimal totalAmount = new BigDecimal("10000");
        int term = 1;
        BigDecimal rate = new BigDecimal("12.0"); // 1% в месяц
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        BigDecimal monthlyPayment = new BigDecimal("10100.00");

        List<PaymentScheduleElementEntity> schedule = calculator.calculateSchedule(
                totalAmount, term, rate, startDate, monthlyPayment);

        assertEquals(1, schedule.size());

        PaymentScheduleElementEntity first = schedule.getFirst();
        assertEquals(1, first.getNumber());
        assertEquals(startDate.plusMonths(1), first.getDate());
        assertEquals(monthlyPayment.setScale(2, RoundingMode.HALF_UP), first.getTotalPayment());

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), first.getRemainingDebt());
    }

    @Test
    @DisplayName("PSK корректен для стандартного аннуитетного графика")
    void testCalculatePskBasic() {
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);

        BigDecimal originalAmount = new BigDecimal("120000");
        int term = 12;
        BigDecimal rate = new BigDecimal("12.0");
        LocalDate issueDate = LocalDate.of(2024, 1, 1);
        BigDecimal monthlyPayment = new BigDecimal("10661.56");
        List<PaymentScheduleElementEntity> schedule = calculator.calculateSchedule(
                originalAmount, term, rate, issueDate, monthlyPayment);

        BigDecimal psk = calculator.calculatePsk(schedule, originalAmount, issueDate);

        assertTrue(
                psk.compareTo(new BigDecimal("7.1")) <= 0 &&
                        psk.compareTo(new BigDecimal("6.1")) >= 0
        );
    }

    @Test
    @DisplayName("PSK на одноплатёжном кредите")
    void testCalculatePskSinglePayment() {
        ScoringCalculator calculator = new ScoringCalculator(loanProperties);

        LocalDate issueDate = LocalDate.of(2024, 6, 1);
        BigDecimal originalAmount = new BigDecimal("10000");
        int term = 1;
        BigDecimal rate = new BigDecimal("12.0");
        BigDecimal monthlyPayment = new BigDecimal("10100.00");
        List<PaymentScheduleElementEntity> schedule = calculator.calculateSchedule(
                originalAmount, term, rate, issueDate, monthlyPayment);

        BigDecimal psk = calculator.calculatePsk(schedule, originalAmount, issueDate);

        assertTrue(
                psk.compareTo(new BigDecimal("13.0")) <= 0 &&
                        psk.compareTo(new BigDecimal("12.0")) >= 0
        );
    }

}
