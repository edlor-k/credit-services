package ru.creditservices.calculator.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.creditservices.calculator.model.enums.EmploymentStatus;

public class WorkStatusValidator implements ConstraintValidator<ValidWorkStatus, EmploymentStatus> {
    @Override
    public boolean isValid(EmploymentStatus employmentStatus, ConstraintValidatorContext constraintValidatorContext) {
        if (employmentStatus == null) return true;
        return !employmentStatus.equals(EmploymentStatus.UNEMPLOYED);
    }
}
