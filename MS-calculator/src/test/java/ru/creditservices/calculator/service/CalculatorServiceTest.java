package ru.creditservices.calculator.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.dto.*;
import ru.creditservices.calculator.model.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

class CalculatorServiceTest {

    private CalculatorService calculatorService;
    private Validator validator;

    @BeforeEach
    void setUp() {
        LoanProperties loanProperties = mock(LoanProperties.class);

        when(loanProperties.getBaseRate()).thenReturn(BigDecimal.valueOf(20));
        when(loanProperties.getInsuranceRate()).thenReturn(BigDecimal.valueOf(5));
        when(loanProperties.getInsuranceCost()).thenReturn(BigDecimal.valueOf(50000));
        when(loanProperties.getSalaryDiscount()).thenReturn(BigDecimal.valueOf(2));

        when(loanProperties.getSelfEmployedIncrease()).thenReturn(BigDecimal.valueOf(2.0));
        when(loanProperties.getBusinessOwnerIncrease()).thenReturn(BigDecimal.valueOf(1.0));
        when(loanProperties.getPositionMiddleDiscount()).thenReturn(BigDecimal.valueOf(2.0));
        when(loanProperties.getPositionTopDiscount()).thenReturn(BigDecimal.valueOf(3.0));
        when(loanProperties.getMaritalMarriedDiscount()).thenReturn(BigDecimal.valueOf(3.0));
        when(loanProperties.getMaritalDivorcedIncrease()).thenReturn(BigDecimal.valueOf(1.0));
        when(loanProperties.getGenderMaleDiscount()).thenReturn(BigDecimal.valueOf(3.0));
        when(loanProperties.getGenderFemaleDiscount()).thenReturn(BigDecimal.valueOf(3.0));
        when(loanProperties.getGenderOtherIncrease()).thenReturn(BigDecimal.valueOf(7.0));

        calculatorService = new CalculatorService(loanProperties);
    }

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory;
        try {
            factory = Validation.buildDefaultValidatorFactory();
        } catch (IllegalStateException ex) {
            throw new RuntimeException("Failed to initialize ValidatorFactory", ex);
        }
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should generate correct loan offers for prescoring")
    void shouldGenerateCorrectLoanOffersForPrescoring() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .term(24)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .email("ivanov@example.com")
                .birthdate(LocalDate.of(1985, 5, 20))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        var offers = calculatorService.prescoring(request);

        assertThat(offers).hasSize(4);

        assertThat(offers).anySatisfy(offer -> {
            assertThat(offer.getTotalAmount()).isEqualTo(BigDecimal.valueOf(550_000));
            assertThat(offer.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(26148),
                    within(BigDecimal.valueOf(1)));
            assertThat(offer.getRate()).isEqualTo(BigDecimal.valueOf(13));
            assertThat(offer.getIsInsuranceEnabled()).isTrue();
            assertThat(offer.getIsSalaryClient()).isTrue();
        });

        assertThat(offers).anySatisfy(offer -> {
            assertThat(offer.getTotalAmount()).isEqualTo(BigDecimal.valueOf(550_000));
            assertThat(offer.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(26667.66),
                    within(BigDecimal.valueOf(1)));
            assertThat(offer.getRate()).isEqualTo(BigDecimal.valueOf(15));
            assertThat(offer.getIsInsuranceEnabled()).isTrue();
            assertThat(offer.getIsSalaryClient()).isFalse();
        });

        assertThat(offers).anySatisfy(offer -> {
            assertThat(offer.getTotalAmount()).isEqualTo(BigDecimal.valueOf(500_000));
            assertThat(offer.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(24962.05),
                    within(BigDecimal.valueOf(1)));
            assertThat(offer.getRate()).isEqualTo(BigDecimal.valueOf(18));
            assertThat(offer.getIsInsuranceEnabled()).isFalse();
            assertThat(offer.getIsSalaryClient()).isTrue();
        });

        assertThat(offers).anySatisfy(offer -> {
            assertThat(offer.getTotalAmount()).isEqualTo(BigDecimal.valueOf(500_000));
            assertThat(offer.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(25447.9),
                    within(BigDecimal.valueOf(1)));
            assertThat(offer.getRate()).isEqualTo(BigDecimal.valueOf(20));
            assertThat(offer.getIsInsuranceEnabled()).isFalse();
            assertThat(offer.getIsSalaryClient()).isFalse();
        });
    }


    @Test
    @DisplayName("Should fail validation for too small amount")
    void shouldFailValidationForTooSmallAmount() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(5))
                .term(24)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .email("ivanov@example.com")
                .birthdate(LocalDate.of(1985, 5, 20))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        var violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Сумма кредита должна быть не менее 20 000");
    }



    @Test
    @DisplayName("Should calculate credit data with expected values for insured salary client")
    void shouldCalculateCorrectlyWithInsuranceAndSalary() {
        ScoringDataDto request = baseRequestBuilder()
                .maritalStatus(MaritalStatus.MARRIED)
                .isInsuranceEnabled(true)
                .build();

        CreditDto credit = calculatorService.calculate(request);

        assertThat(credit.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(48098.31),
                within(BigDecimal.valueOf(1.0)));
        assertThat(credit.getRate()).isEqualTo(BigDecimal.valueOf(9.0));
        assertThat(credit.getPsk()).isCloseTo(BigDecimal.valueOf(31.019),
                within(BigDecimal.valueOf(0.1)));
    }

    @Test
    @DisplayName("Should calculate credit data for no insurance and salary client")
    void shouldCalculateCorrectlyWithoutInsuranceAndSalary() {
        ScoringDataDto request = baseRequestBuilder()
                .maritalStatus(MaritalStatus.MARRIED)
                .isInsuranceEnabled(false)
                .build();

        CreditDto credit = calculatorService.calculate(request);

        assertThat(credit.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(44893.56),
                within(BigDecimal.valueOf(1.0)));
        assertThat(credit.getRate()).isEqualTo(BigDecimal.valueOf(14.0));
        assertThat(credit.getPsk()).isCloseTo(BigDecimal.valueOf(14.91),
                within(BigDecimal.valueOf(0.1)));
    }

    @Test
    @DisplayName("Should calculate credit data for divorced male")
    void shouldCalculateCorrectlyForDivorcedMale() {
        ScoringDataDto request = baseRequestBuilder()
                .maritalStatus(MaritalStatus.DIVORCED)
                .gender(Gender.MALE)
                .isInsuranceEnabled(false)
                .build();

        CreditDto credit = calculatorService.calculate(request);

        assertThat(credit.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(45840),
                within(BigDecimal.valueOf(1.0)));
        assertThat(credit.getRate()).isEqualTo(BigDecimal.valueOf(18.0));
        assertThat(credit.getPsk()).isCloseTo(BigDecimal.valueOf(19.529),
                within(BigDecimal.valueOf(0.1)));
    }

    @Test
    @DisplayName("Should calculate credit data for divorced self-employed other")
    void shouldCalculateCorrectlyForDivorcedOther() {
        ScoringDataDto request = baseRequestBuilder()
                .maritalStatus(MaritalStatus.DIVORCED)
                .gender(Gender.OTHER)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                        .employmentINN("1234567890")
                        .salary(BigDecimal.valueOf(120000))
                        .position(Position.SENIOR)
                        .workExperienceTotal(24)
                        .workExperienceCurrent(6)
                        .build())
                .isInsuranceEnabled(false)
                .build();

        CreditDto credit = calculatorService.calculate(request);

        assertThat(credit.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(48008.7),
                within(BigDecimal.valueOf(1.0)));
        assertThat(credit.getRate()).isEqualTo(BigDecimal.valueOf(27.0));
        assertThat(credit.getPsk()).isCloseTo(BigDecimal.valueOf(30.55),
                within(BigDecimal.valueOf(0.1)));
    }

    @Test
    @DisplayName("Should calculate credit data for divorced business owner female")
    void shouldCalculateCorrectlyForDivorcedBusinessOwnerFemale() {
        ScoringDataDto request = baseRequestBuilder()
                .firstName("Анна")
                .lastName("Иванова")
                .middleName("Ивановна")
                .gender(Gender.FEMALE)
                .maritalStatus(MaritalStatus.DIVORCED)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
                        .employmentINN("1234567890")
                        .salary(BigDecimal.valueOf(120000))
                        .position(Position.MIDDLE)
                        .workExperienceTotal(24)
                        .workExperienceCurrent(6)
                        .build())
                .isInsuranceEnabled(false)
                .build();

        CreditDto credit = calculatorService.calculate(request);

        assertThat(credit.getMonthlyPayment()).isCloseTo(BigDecimal.valueOf(45602.38),
                within(BigDecimal.valueOf(1.0)));
        assertThat(credit.getRate()).isEqualTo(BigDecimal.valueOf(17.0));
        assertThat(credit.getPsk()).isCloseTo(BigDecimal.valueOf(18.358),
                within(BigDecimal.valueOf(0.1)));
    }

    private ScoringDataDto.ScoringDataDtoBuilder baseRequestBuilder() {
        return ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .term(12)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
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
                        .salary(BigDecimal.valueOf(120000))
                        .position(Position.JUNIOR)
                        .workExperienceTotal(24)
                        .workExperienceCurrent(6)
                        .build())
                .accountNumber("12345678901234567000")
                .isInsuranceEnabled(true)
                .isSalaryClient(true);
    }
}
