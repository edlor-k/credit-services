package ru.creditservices.calculator.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.exception.LoanPrescoringException;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;
import ru.creditservices.calculator.service.prescoring.LoanPrescoringValidator;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LoanPrescoringValidationTest {

    @Test
    @DisplayName("Корректные данные не должны вызывать исключение")
    void testValidEntityShouldPass() {
        LoanPrescoringValidator validator = new LoanPrescoringValidator();
        LoanStatementEntity entity = LoanStatementEntity.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("Alexeevich")
                .amount(new BigDecimal("30000"))
                .term(12)
                .birthdate(LocalDate.now().minusYears(25))
                .email("ivan_petrov@mail.ru")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        assertDoesNotThrow(() -> validator.validate(entity));
    }

    @Test
    @DisplayName("Некорректные данные должны вызывать исключение")
    void testInvalidParametersShouldThrowException() {
        LoanPrescoringValidator validator = new LoanPrescoringValidator();
        LoanStatementEntity entity = LoanStatementEntity.builder()
                .firstName("123")
                .lastName("123")
                .middleName("123")
                .amount(new BigDecimal("3"))
                .term(1)
                .birthdate(LocalDate.now().minusYears(1))
                .email("123")
                .passportSeries("123")
                .passportNumber("567")
                .build();

        LoanPrescoringException ex = assertThrows(
                LoanPrescoringException.class,
                () -> validator.validate(entity)
        );

        String msg = ex.getMessage().toLowerCase();

        assertTrue(
            msg.contains("имя") &&
            msg.contains("фамил") &&
            msg.contains("отчеств") &&
            msg.contains("сумм") &&
            msg.contains("срок") &&
            msg.contains("старше") &&
            msg.contains("email") &&
            msg.contains("сер") &&
            msg.contains("номер")
        );
    }
}
