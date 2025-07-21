package ru.creditservices.statement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.creditservices.statement.exception.PrescoringBusinessException;
import ru.creditservices.statement.model.entity.LoanStatementEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PrescoringServiceImplTest {

    private PrescoringServiceImpl prescoringService;

    @BeforeEach
    void setUp() {
        prescoringService = new PrescoringServiceImpl();
    }

    private LoanStatementEntity validEntity() {
        return LoanStatementEntity.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .amount(BigDecimal.valueOf(20000))
                .term(12)
                .birthdate(LocalDate.now().minusYears(25))
                .email("ivanov@mail.com")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();
    }

    @Test
    void validateValidEntityNoException() {
        assertDoesNotThrow(() -> prescoringService.businessValidate(validEntity()));
    }

    @Test
    void validateInvalidFirstNameThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setFirstName("Иван123");
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("firstName"));
    }

    @Test
    void validateInvalidLastNameThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setLastName("");
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("lastName"));
    }

    @Test
    void validateInvalidMiddleNameThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setMiddleName("!@#$%");
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("middleName"));
    }

    @Test
    void validateLowAmountThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setAmount(BigDecimal.valueOf(10000));
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("amount"));
    }

    @Test
    void validateLowTermThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setTerm(3);
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("term"));
    }

    @Test
    void validateYoungAgeThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setBirthdate(LocalDate.now().minusYears(16));
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("birthdate"));
    }

    @Test
    void validateInvalidEmailThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setEmail("not-an-email");
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("email"));
    }

    @Test
    void validateInvalidPassportSeriesThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setPassportSeries("12AB");
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("passportSeries"));
    }

    @Test
    void validateInvalidPassportNumberThrowsException() {
        LoanStatementEntity entity = validEntity();
        entity.setPassportNumber("ABC123");
        PrescoringBusinessException ex = assertThrows(PrescoringBusinessException.class,
                () -> prescoringService.businessValidate(entity));
        assertTrue(ex.getDetails().containsKey("passportNumber"));
    }
}
