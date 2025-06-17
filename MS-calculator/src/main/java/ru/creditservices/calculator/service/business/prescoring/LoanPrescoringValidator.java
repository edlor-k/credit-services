package ru.creditservices.calculator.service.business.prescoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.exception.LoanPrescoringException;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;

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

    public void validate(LoanStatementEntity entity) {
        log.info("[LoanPrescoringValidator] Start validation: {}", entity);

        if (!NAME_PATTERN.matcher(entity.getFirstName()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid firstName: {}", entity.getFirstName());
            throw new LoanPrescoringException(PRESCORING_FIRSTNAME_INVALID);
        }
        if (!NAME_PATTERN.matcher(entity.getLastName()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid lastName: {}", entity.getLastName());
            throw new LoanPrescoringException(PRESCORING_LASTNAME_INVALID);
        }
        if (entity.getMiddleName() != null && !entity.getMiddleName().isEmpty()) {
            if (!NAME_PATTERN.matcher(entity.getMiddleName()).matches()) {
                log.warn("[LoanPrescoringValidator] Invalid middleName: {}", entity.getMiddleName());
                throw new LoanPrescoringException(PRESCORING_MIDDLENAME_INVALID);
            }
        }
        if (entity.getAmount() == null || entity.getAmount().longValue() < 20000) {
            log.warn("[LoanPrescoringValidator] Invalid amount: {}", entity.getAmount());
            throw new LoanPrescoringException(PRESCORING_AMOUNT_INVALID);
        }
        if (entity.getTerm() == null || entity.getTerm() < 6) {
            log.warn("[LoanPrescoringValidator] Invalid term: {}", entity.getTerm());
            throw new LoanPrescoringException(PRESCORING_TERM_INVALID);
        }
        if (entity.getBirthdate() == null) {
            log.warn("[LoanPrescoringValidator] Birthdate is null");
            throw new LoanPrescoringException(PRESCORING_BIRTHDATE_REQUIRED);
        }
        int age = Period.between(entity.getBirthdate(), LocalDate.now()).getYears();
        if (age < 18) {
            log.warn("[LoanPrescoringValidator] Too young (age {}): {}", age, entity.getBirthdate());
            throw new LoanPrescoringException(PRESCORING_AGE_INVALID);
        }
        if (entity.getEmail() == null || !EMAIL_PATTERN.matcher(entity.getEmail()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid email: {}", entity.getEmail());
            throw new LoanPrescoringException(PRESCORING_EMAIL_INVALID);
        }
        if (entity.getPassportSeries() == null || !PASSPORT_SERIES_PATTERN.matcher(entity.getPassportSeries()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid passportSeries: {}", entity.getPassportSeries());
            throw new LoanPrescoringException(PRESCORING_PASSPORT_SERIES_INVALID);
        }
        if (entity.getPassportNumber() == null || !PASSPORT_NUMBER_PATTERN.matcher(entity.getPassportNumber()).matches()) {
            log.warn("[LoanPrescoringValidator] Invalid passportNumber: {}", entity.getPassportNumber());
            throw new LoanPrescoringException(PRESCORING_PASSPORT_NUMBER_INVALID);
        }

        log.info("[LoanPrescoringValidator] Validation PASSED for: {}", entity);
    }
}
