package ru.creditservices.statement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.statement.exception.PrescoringBusinessException;
import ru.creditservices.statement.model.entity.LoanStatementEntity;
import ru.creditservices.statement.service.PrescoringService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static ru.creditservices.statement.util.RegexPatternsUtil.*;
import static ru.creditservices.statement.util.ErrorMessagesUtil.*;

@Service
@Slf4j
public class PrescoringServiceImpl implements PrescoringService {
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL);
    private static final Pattern PASSPORT_SERIES_PATTERN = Pattern.compile(PASSPORT_SERIES);
    private static final Pattern PASSPORT_NUMBER_PATTERN = Pattern.compile(PASSPORT_NUMBER);

    @Override
    public void businessValidate(LoanStatementEntity entity) {
        log.info("Start validation: {}", entity);

        List<String> errors = new ArrayList<>();

        validateName(entity.getFirstName(), PRESCORING_FIRSTNAME_INVALID).ifPresent(errors::add);
        validateName(entity.getLastName(), PRESCORING_LASTNAME_INVALID).ifPresent(errors::add);
        validateMiddleName(entity.getMiddleName()).ifPresent(errors::add);

        validateAmount(entity.getAmount()).ifPresent(errors::add);
        validateTerm(entity.getTerm()).ifPresent(errors::add);
        validateBirthdate(entity.getBirthdate()).ifPresent(errors::add);
        validateEmail(entity.getEmail()).ifPresent(errors::add);
        validatePassportSeries(entity.getPassportSeries()).ifPresent(errors::add);
        validatePassportNumber(entity.getPassportNumber()).ifPresent(errors::add);

        if (!errors.isEmpty()) {
            String errorMessage = String.join(",\n", errors);
            log.error("Validation FAILED with errors: {}", errorMessage);
            throw new PrescoringBusinessException(errorMessage);
        }

        log.debug("Validation PASSED for: {}", entity);
    }

    private Optional<String> validateName(String name, String errorMsg) {
        return (name == null || !NAME_PATTERN.matcher(name).matches())
                ? Optional.of(errorMsg) : Optional.empty();
    }

    private Optional<String> validateMiddleName(String middleName) {
        if (middleName == null || middleName.isEmpty()) return Optional.empty();
        return !NAME_PATTERN.matcher(middleName).matches()
                ? Optional.of(PRESCORING_MIDDLENAME_INVALID) : Optional.empty();
    }

    private Optional<String> validateAmount(Number amount) {
        return (amount == null || amount.longValue() < 20000)
                ? Optional.of(PRESCORING_AMOUNT_INVALID) : Optional.empty();
    }

    private Optional<String> validateTerm(Integer term) {
        return (term == null || term < 6) ? Optional.of(PRESCORING_TERM_INVALID) : Optional.empty();
    }

    private Optional<String> validateBirthdate(LocalDate birthdate) {
        if (birthdate == null) return Optional.of(PRESCORING_BIRTHDATE_REQUIRED);
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        return (age < 18) ? Optional.of(PRESCORING_AGE_INVALID) : Optional.empty();
    }

    private Optional<String> validateEmail(String email) {
        return (email == null || !EMAIL_PATTERN.matcher(email).matches())
                ? Optional.of(PRESCORING_EMAIL_INVALID) : Optional.empty();
    }

    private Optional<String> validatePassportSeries(String series) {
        return (series == null || !PASSPORT_SERIES_PATTERN.matcher(series).matches())
                ? Optional.of(PRESCORING_PASSPORT_SERIES_INVALID) : Optional.empty();
    }

    private Optional<String> validatePassportNumber(String number) {
        return (number == null || !PASSPORT_NUMBER_PATTERN.matcher(number).matches())
                ? Optional.of(PRESCORING_PASSPORT_NUMBER_INVALID) : Optional.empty();
    }
}

