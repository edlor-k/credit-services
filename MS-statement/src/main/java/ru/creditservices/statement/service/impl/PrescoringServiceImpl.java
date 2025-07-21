package ru.creditservices.statement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.creditservices.statement.exception.PrescoringBusinessException;
import ru.creditservices.statement.model.entity.LoanStatementEntity;
import ru.creditservices.statement.service.PrescoringService;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;
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
        log.info("Start prescoring validation: {}", entity);

        Map<String, String> errors = new LinkedHashMap<>();

        validateField(entity.getFirstName(), NAME_PATTERN, "firstName", PRESCORING_FIRSTNAME_INVALID, errors);
        validateField(entity.getLastName(), NAME_PATTERN, "lastName", PRESCORING_LASTNAME_INVALID, errors);
        validateMiddleName(entity.getMiddleName(), errors);

        validateAmount(entity.getAmount(), errors);
        validateTerm(entity.getTerm(), errors);
        validateBirthdate(entity.getBirthdate(), errors);
        validateField(entity.getEmail(), EMAIL_PATTERN, "email", PRESCORING_EMAIL_INVALID, errors);
        validateField(entity.getPassportSeries(), PASSPORT_SERIES_PATTERN, "passportSeries", PRESCORING_PASSPORT_SERIES_INVALID, errors);
        validateField(entity.getPassportNumber(), PASSPORT_NUMBER_PATTERN, "passportNumber", PRESCORING_PASSPORT_NUMBER_INVALID, errors);

        if (!errors.isEmpty()) {
            log.error("Prescoring validation failed with errors: {}", errors);
            throw new PrescoringBusinessException("Ошибка бизнес-валидации анкеты", errors);
        }

        log.debug("Prescoring validation passed for entity: {}", entity);
    }

    private void validateField(String value, Pattern pattern, String field, String errorMsg, Map<String, String> errors) {
        if (value == null || !pattern.matcher(value).matches()) {
            errors.put(field, errorMsg);
        }
    }

    private void validateMiddleName(String middleName, Map<String, String> errors) {
        if (middleName != null && !middleName.isEmpty() && !NAME_PATTERN.matcher(middleName).matches()) {
            errors.put("middleName", PRESCORING_MIDDLENAME_INVALID);
        }
    }

    private void validateAmount(Number amount, Map<String, String> errors) {
        if (amount == null || amount.longValue() < 20000) {
            errors.put("amount", PRESCORING_AMOUNT_INVALID);
        }
    }

    private void validateTerm(Integer term, Map<String, String> errors) {
        if (term == null || term < 6) {
            errors.put("term", PRESCORING_TERM_INVALID);
        }
    }

    private void validateBirthdate(LocalDate birthdate, Map<String, String> errors) {
        if (birthdate == null) {
            errors.put("birthdate", PRESCORING_BIRTHDATE_REQUIRED);
        } else {
            int age = Period.between(birthdate, LocalDate.now()).getYears();
            if (age < 18) {
                errors.put("birthdate", PRESCORING_AGE_INVALID);
            }
        }
    }
}
