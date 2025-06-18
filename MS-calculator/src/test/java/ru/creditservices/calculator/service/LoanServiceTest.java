package ru.creditservices.calculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.exception.LoanPrescoringException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @Test
    void correctDataShouldReturnFourOffersWithExpectedFlags() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(12)
                .firstName("John")
                .lastName("Smith")
                .email("johnsm@gmail.com")
                .birthdate(LocalDate.parse("1980-01-01"))
                .passportSeries("2222")
                .passportNumber("333333")
                .build();

        List<LoanOfferDto> offers = loanService.getLoanOffers(request);

        assertEquals(4, offers.size());

        assertTrue(containsCombination(offers, true, true));
        assertTrue(containsCombination(offers, true, false));
        assertTrue(containsCombination(offers, false, true));
        assertTrue(containsCombination(offers, false, false));
    }

    @Test
    void invalidAmountShouldThrowException() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .term(12)
                .firstName("John")
                .lastName("Smith")
                .email("johnsm@gmail.com")
                .birthdate(LocalDate.parse("1980-01-01"))
                .passportSeries("2222")
                .passportNumber("333333")
                .build();

        LoanPrescoringException exception = assertThrows(
                LoanPrescoringException.class,
                () -> loanService.getLoanOffers(request)
        );
        assertTrue(exception.getMessage().contains("Сумма кредита"));
    }

    @Test
    void invalidTermShouldThrowException() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(30000))
                .term(3)
                .firstName("John")
                .lastName("Smith")
                .email("johnsm@gmail.com")
                .birthdate(LocalDate.parse("1980-01-01"))
                .passportSeries("2222")
                .passportNumber("333333")
                .build();

        LoanPrescoringException exception = assertThrows(
                LoanPrescoringException.class,
                () -> loanService.getLoanOffers(request)
        );
        assertTrue(exception.getMessage().contains("Срок кредита"));
    }

    @Test
    void underageClientShouldThrowException() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(30000))
                .term(12)
                .firstName("John")
                .lastName("Smith")
                .email("johnsm@gmail.com")
                .birthdate(LocalDate.now().minusYears(17))
                .passportSeries("2222")
                .passportNumber("333333")
                .build();

        LoanPrescoringException exception = assertThrows(
                LoanPrescoringException.class,
                () -> loanService.getLoanOffers(request)
        );
        assertTrue(exception.getMessage().contains("старше"));
    }

    @Test
    void invalidEmailShouldThrowException() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(30000))
                .term(12)
                .firstName("John")
                .lastName("Smith")
                .email("not-an-email")
                .birthdate(LocalDate.parse("1980-01-01"))
                .passportSeries("2222")
                .passportNumber("333333")
                .build();

        LoanPrescoringException exception = assertThrows(
                LoanPrescoringException.class,
                () -> loanService.getLoanOffers(request)
        );
        assertTrue(exception.getMessage().toLowerCase().contains("email"));
    }

    @Test
    void invalidPassportSeriesShouldThrowException() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(30000))
                .term(12)
                .firstName("John")
                .lastName("Smith")
                .email("johnsm@gmail.com")
                .birthdate(LocalDate.parse("1980-01-01"))
                .passportSeries("abcd")
                .passportNumber("333333")
                .build();

        LoanPrescoringException exception = assertThrows(
                LoanPrescoringException.class,
                () -> loanService.getLoanOffers(request)
        );
        assertTrue(exception.getMessage().toLowerCase().contains("серия"));
    }

    private boolean containsCombination(List<LoanOfferDto> offers, boolean insurance, boolean salary) {
        return offers.stream().anyMatch(
                offer -> offer.getIsInsuranceEnabled() == insurance && offer.getIsSalaryClient() == salary
        );
    }
}
