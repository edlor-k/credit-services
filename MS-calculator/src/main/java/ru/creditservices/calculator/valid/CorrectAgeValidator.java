package ru.creditservices.calculator.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class CorrectAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {
    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext constraintValidatorContext) {
        if (birthdate == null) return true;
        int period = Period.between(birthdate, LocalDate.now()).getYears();
        return period >= 20 && period <= 65;
    }
}
