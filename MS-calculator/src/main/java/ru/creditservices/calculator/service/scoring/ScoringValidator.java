package ru.creditservices.calculator.service.scoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditservices.calculator.exception.ScoringException;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.model.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static ru.creditservices.calculator.util.ErrorMessagesUtil.*;

@Slf4j
@Component
public class ScoringValidator {

    public void validate(ScoringDataEntity data) {

        List<String> errors = new ArrayList<>();

        log.info("Start validation: {}", data);

        if (data.getBirthdate() == null)
            errors.add(SCORING_BIRTHDATE_REQUIRED);
        else {
            int age = Period.between(data.getBirthdate(), LocalDate.now()).getYears();
            if (age < 20 || age > 65)
                errors.add(SCORING_INVALID_AGE);
        }

        if (data.getEmployment() == null)
            errors.add(SCORING_EMPLOYMENT_REQUIRED);
        else {
            if (data.getEmployment().getWorkExperienceTotal() == null)
                errors.add(SCORING_TOTAL_EXPERIENCE_REQUIRED);
            else if (data.getEmployment().getWorkExperienceTotal() < 18)
                errors.add(SCORING_TOTAL_EXPERIENCE_TOO_SMALL);

            if (data.getEmployment().getWorkExperienceCurrent() == null)
                errors.add(SCORING_CURRENT_EXPERIENCE_REQUIRED);
            else if (data.getEmployment().getWorkExperienceCurrent() < 3)
                errors.add(SCORING_CURRENT_EXPERIENCE_TOO_SMALL);

            if (data.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED)
                errors.add(SCORING_UNEMPLOYED);

            if (data.getEmployment().getSalary() == null)
                errors.add(SCORING_SALARY_REQUIRED);

            if (data.getAmount() == null)
                errors.add(SCORING_AMOUNT_REQUIRED);
            else if (data.getEmployment().getSalary() != null) {
                BigDecimal maxAmount = data.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));
                if (data.getAmount().compareTo(maxAmount) > 0)
                    errors.add(SCORING_AMOUNT_TOO_LARGE);
            }
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.join(",\n", errors);
            log.warn("Validation FAILED with errors: {}", errorMessage);
            errors.clear();
            throw new ScoringException(errorMessage);
        }

        log.info("Validation PASSED for: {}", data);
    }
}
