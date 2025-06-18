package ru.creditservices.calculator.service.prescoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.exception.LoanPrescoringException;
import ru.creditservices.calculator.model.entity.LoanStatementEntity;

import static ru.creditservices.calculator.util.RegexPatternsUtil.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
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

        List<String> errors = new ArrayList<>();
        log.info("Start validation: {}", entity);

        if (!NAME_PATTERN.matcher(entity.getFirstName()).matches())
            errors.add(PRESCORING_FIRSTNAME_INVALID);

        if (!NAME_PATTERN.matcher(entity.getLastName()).matches())
            errors.add(PRESCORING_LASTNAME_INVALID);

        if (entity.getMiddleName() != null && !entity.getMiddleName().isEmpty())
            if (!NAME_PATTERN.matcher(entity.getMiddleName()).matches())
                errors.add(PRESCORING_MIDDLENAME_INVALID);

        if (entity.getAmount() == null || entity.getAmount().longValue() < 20000)
            errors.add(PRESCORING_AMOUNT_INVALID);

        if (entity.getTerm() == null || entity.getTerm() < 6)
            errors.add(PRESCORING_TERM_INVALID);

        if (entity.getBirthdate() == null)
            errors.add(PRESCORING_BIRTHDATE_REQUIRED);
        else {
            int age = Period.between(entity.getBirthdate(), LocalDate.now()).getYears();
            if (age < 18)
                errors.add(PRESCORING_AGE_INVALID);
        }

        if (entity.getEmail() == null || !EMAIL_PATTERN.matcher(entity.getEmail()).matches())
            errors.add(PRESCORING_EMAIL_INVALID);

        if (entity.getPassportSeries() == null
                || !PASSPORT_SERIES_PATTERN.matcher(entity.getPassportSeries()).matches())
            errors.add(PRESCORING_PASSPORT_SERIES_INVALID);

        if (entity.getPassportNumber() == null
                || !PASSPORT_NUMBER_PATTERN.matcher(entity.getPassportNumber()).matches())
            errors.add(PRESCORING_PASSPORT_NUMBER_INVALID);

        if (!errors.isEmpty()) {
            String errorMessage = String.join(",\n", errors);
            log.error("Validation FAILED with errors: {}", errorMessage);
            errors.clear();
            throw new LoanPrescoringException(errorMessage);
        }

        log.info("Validation PASSED for: {}", entity);
    }
}
