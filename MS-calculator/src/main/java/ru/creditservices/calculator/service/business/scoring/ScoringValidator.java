package ru.creditservices.calculator.service.business.scoring;

import org.springframework.stereotype.Component;
import ru.creditservices.calculator.exception.ScoringException;
import ru.creditservices.calculator.model.entity.ScoringDataEntity;
import ru.creditservices.calculator.model.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static ru.creditservices.calculator.util.ErrorMessagesUtil.*;

@Component
public class ScoringValidator {
    public void validate(ScoringDataEntity data) {
        if (data.getBirthdate() == null) {
            throw new ScoringException(SCORING_BIRTHDATE_REQUIRED);
        }
        int age = Period.between(data.getBirthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            throw new ScoringException(SCORING_INVALID_AGE);
        }

        if (data.getEmployment() == null) {
            throw new ScoringException(SCORING_EMPLOYMENT_REQUIRED);
        }
        if (data.getEmployment().getWorkExperienceTotal() == null) {
            throw new ScoringException(SCORING_TOTAL_EXPERIENCE_REQUIRED);
        }
        if (data.getEmployment().getWorkExperienceTotal() < 18) {
            throw new ScoringException(SCORING_TOTAL_EXPERIENCE_TOO_SMALL);
        }
        if (data.getEmployment().getWorkExperienceCurrent() == null) {
            throw new ScoringException(SCORING_CURRENT_EXPERIENCE_REQUIRED);
        }
        if (data.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new ScoringException(SCORING_CURRENT_EXPERIENCE_TOO_SMALL);
        }
        if (data.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new ScoringException(SCORING_UNEMPLOYED);
        }
        if (data.getAmount() == null || data.getEmployment().getSalary() == null) {
            throw new ScoringException(SCORING_AMOUNT_OR_SALARY_REQUIRED);
        }
        BigDecimal maxAmount = data.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));
        if (data.getAmount().compareTo(maxAmount) > 0) {
            throw new ScoringException(SCORING_AMOUNT_TOO_LARGE);
        }
    }
}
