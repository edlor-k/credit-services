package ru.creditservices.calculator.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.exception.LoanPrescoringException;
import ru.creditservices.calculator.service.business.prescoring.LoanOfferCalculator;
import ru.creditservices.calculator.service.business.prescoring.LoanPrescoringValidator;
import ru.creditservices.calculator.service.impl.LoanService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    private LoanService loanService;
    private LoanPrescoringValidator prescoringValidator;
    private LoanOfferCalculator loanOfferCalculator;
    private Validator validator;

    @BeforeEach
    void setUp() {
        LoanProperties loanProperties = mock(LoanProperties.class);
        prescoringValidator = mock(LoanPrescoringValidator.class);
        loanOfferCalculator = mock(LoanOfferCalculator.class);

        loanService = new LoanService(
                loanProperties,
                prescoringValidator,
                loanOfferCalculator
        );

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        when(loanProperties.getBaseRate()).thenReturn(BigDecimal.valueOf(20));
        when(loanProperties.getInsuranceRate()).thenReturn(BigDecimal.valueOf(5));
        when(loanProperties.getInsuranceCost()).thenReturn(BigDecimal.valueOf(50000));
        when(loanProperties.getSalaryDiscount()).thenReturn(BigDecimal.valueOf(2));
    }

    @Test
    @DisplayName("Должен корректно возвращать 4 предложения по кредиту")
    void shouldGenerateCorrectLoanOffersForPrescoring() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(500_000))
                .term(24)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivanov@example.com")
                .birthdate(LocalDate.of(1985, 5, 20))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        for (boolean insurance : new boolean[]{true, false}) {
            for (boolean salary : new boolean[]{true, false}) {
                when(loanOfferCalculator.buildLoanOffer(
                        any(UUID.class),
                        eq(BigDecimal.valueOf(500_000)),
                        eq(24),
                        eq(BigDecimal.valueOf(20)),
                        eq(BigDecimal.valueOf(5)),
                        eq(BigDecimal.valueOf(50000)),
                        eq(BigDecimal.valueOf(2)),
                        eq(insurance),
                        eq(salary)
                )).thenReturn(
                        LoanOfferDto.builder()
                                .statementId(UUID.randomUUID())
                                .requestedAmount(BigDecimal.valueOf(500_000))
                                .totalAmount(insurance ? BigDecimal.valueOf(550_000) : BigDecimal.valueOf(500_000))
                                .term(24)
                                .monthlyPayment(BigDecimal.valueOf(25000))
                                .rate(BigDecimal.valueOf(insurance ? 15 : 20))
                                .isInsuranceEnabled(insurance)
                                .isSalaryClient(salary)
                                .build()
                );
            }
        }

        List<LoanOfferDto> offers = loanService.getLoanOffers(request);

        assertThat(offers)
                .isNotNull()
                .hasSize(4)
                .allSatisfy(offer -> {
                    assertThat(offer.getRequestedAmount()).isEqualTo(BigDecimal.valueOf(500_000));
                    assertThat(offer.getTerm()).isEqualTo(24);
                });
    }

    @Test
    @DisplayName("Должен бросать LoanPrescoringException при некорректных данных")
    void shouldThrowOnInvalidInput() {
        LoanStatementRequestDto badRequest = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .term(1)
                .firstName("A")
                .lastName("B")
                .email("fail@fail")
                .birthdate(LocalDate.of(2010, 1, 1))
                .passportSeries("12")
                .passportNumber("123")
                .build();

        doThrow(new LoanPrescoringException("Ошибка!"))
                .when(prescoringValidator).validate(any());

        assertThatThrownBy(() -> loanService.getLoanOffers(badRequest))
                .isInstanceOf(LoanPrescoringException.class);
    }

    @Test
    @DisplayName("Должен возвращать ошибки валидации Bean Validation для не положительной суммы")
    void shouldFailValidationForTooSmallAmount() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(0))
                .term(24)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivanov@example.com")
                .birthdate(LocalDate.of(1985, 5, 20))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Сумма кредита должна быть положительной");
    }
}
