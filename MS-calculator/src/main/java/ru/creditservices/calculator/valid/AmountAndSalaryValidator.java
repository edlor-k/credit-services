package ru.creditservices.calculator.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.creditservices.calculator.dto.ScoringDataDto;

import java.math.BigDecimal;

public class AmountAndSalaryValidator implements ConstraintValidator<ValidAmountAndSalary, ScoringDataDto> {
    @Override
    public boolean isValid(ScoringDataDto scoringData, ConstraintValidatorContext constraintValidatorContext) {
        if (scoringData == null
                || scoringData.getAmount() == null
                || scoringData.getEmployment() == null
                || scoringData.getEmployment().getSalary() == null) {
            return true;
        }
        BigDecimal maxAmount = scoringData.getEmployment().getSalary()
                .multiply(BigDecimal.valueOf(24));

        return scoringData.getAmount().compareTo(maxAmount) <= 0;
    }
}
