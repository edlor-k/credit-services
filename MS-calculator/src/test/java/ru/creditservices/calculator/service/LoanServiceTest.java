package ru.creditservices.calculator.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.exception.LoanPrescoringException;
import ru.creditservices.calculator.mapper.LoanOfferMapper;
import ru.creditservices.calculator.mapper.LoanStatementMapper;
import ru.creditservices.calculator.service.business.prescoring.LoanOfferCalculator;
import ru.creditservices.calculator.service.business.prescoring.LoanPrescoringValidator;
import ru.creditservices.calculator.service.impl.LoanService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
class LoanServiceTest {

    private LoanService loanService;
    private LoanPrescoringValidator prescoringValidator;
    private Validator validator;

    @BeforeEach
    void setUp() {
        LoanProperties loanProperties = mock(LoanProperties.class);
        prescoringValidator = mock(LoanPrescoringValidator.class);
        LoanOfferCalculator loanOfferCalculator = mock(LoanOfferCalculator.class);

        LoanStatementMapper statementMapper = mock(LoanStatementMapper.class);
        LoanOfferMapper offerMapper = mock(LoanOfferMapper.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        when(loanProperties.getBaseRate()).thenReturn(BigDecimal.valueOf(20));
        when(loanProperties.getInsuranceRate()).thenReturn(BigDecimal.valueOf(5));
        when(loanProperties.getInsuranceCost()).thenReturn(BigDecimal.valueOf(50000));
        when(loanProperties.getSalaryDiscount()).thenReturn(BigDecimal.valueOf(2));

        loanService = new LoanService(
                loanProperties,
                prescoringValidator,
                loanOfferCalculator,
                statementMapper,
                offerMapper
        );
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
