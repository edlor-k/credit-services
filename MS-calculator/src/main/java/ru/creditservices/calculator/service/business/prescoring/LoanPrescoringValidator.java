package ru.creditservices.calculator.service.business.prescoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.exception.LoanPrescoringException;

import static ru.creditservices.calculator.util.RegexPatternsUtil.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

import static ru.creditservices.calculator.util.ErrorMessagesUtil.*;

@Slf4j
@Component
public class LoanPrescoringValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile(NAME);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL);
    private static final Pattern PASSPORT_SERIES_PATTERN = Pattern.compile(PASSPORT_SERIES);
    private static final Pattern PASSPORT_NUMBER_PATTERN = Pattern.compile(PASSPORT_NUMBER);

    public void validate(LoanStatementRequestDto dto) {
        log.info("[LoanPrescoringValidator] Start validation: {}", dto);

        if (!NAME_PATTERN.matcher(dto.getFirstName()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid firstName: {}", dto.getFirstName());
            throw new LoanPrescoringException(PRESCORING_FIRSTNAME_INVALID);
        }
        if (!NAME_PATTERN.matcher(dto.getLastName()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid lastName: {}", dto.getLastName());
            throw new LoanPrescoringException(PRESCORING_LASTNAME_INVALID);
        }
        if (dto.getMiddleName() != null && !dto.getMiddleName().isEmpty()) {
            if (!NAME_PATTERN.matcher(dto.getMiddleName()).matches()) {
                log.warn("[LoanPrescoringValidator] Invalid middleName: {}", dto.getMiddleName());
                throw new LoanPrescoringException(PRESCORING_MIDDLENAME_INVALID);
            }
        }
        if (dto.getAmount() == null || dto.getAmount().longValue() < 20000) {
            log.warn("[LoanPrescoringValidator] Invalid amount: {}", dto.getAmount());
            throw new LoanPrescoringException(PRESCORING_AMOUNT_INVALID);
        }
        if (dto.getTerm() == null || dto.getTerm() < 6) {
            log.warn("[LoanPrescoringValidator] Invalid term: {}", dto.getTerm());
            throw new LoanPrescoringException(PRESCORING_TERM_INVALID);
        }
        if (dto.getBirthdate() == null) {
            log.warn("[LoanPrescoringValidator] Birthdate is null");
            throw new LoanPrescoringException(PRESCORING_BIRTHDATE_REQUIRED);
        }
        int age = Period.between(dto.getBirthdate(), LocalDate.now()).getYears();
        if (age < 18) {
            log.warn("[LoanPrescoringValidator] Too young (age {}): {}", age, dto.getBirthdate());
            throw new LoanPrescoringException(PRESCORING_AGE_INVALID);
        }
        if (dto.getEmail() == null || !EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid email: {}", dto.getEmail());
            throw new LoanPrescoringException(PRESCORING_EMAIL_INVALID);
        }
        if (dto.getPassportSeries() == null || !PASSPORT_SERIES_PATTERN.matcher(dto.getPassportSeries()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid passportSeries: {}", dto.getPassportSeries());
            throw new LoanPrescoringException(PRESCORING_PASSPORT_SERIES_INVALID);
        }
        if (dto.getPassportNumber() == null || !PASSPORT_NUMBER_PATTERN.matcher(dto.getPassportNumber()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid passportNumber: {}", dto.getPassportNumber());
            throw new LoanPrescoringException(PRESCORING_PASSPORT_NUMBER_INVALID);
        }

        log.info("[LoanPrescoringValidator] Validation PASSED for: {}", dto);
    }
}
