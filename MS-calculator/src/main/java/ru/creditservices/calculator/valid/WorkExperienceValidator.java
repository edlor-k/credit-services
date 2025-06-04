package ru.creditservices.calculator.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.creditservices.calculator.dto.EmploymentDto;

public class WorkExperienceValidator implements ConstraintValidator<ValidWorkExperience, EmploymentDto> {

    @Override
    public boolean isValid(EmploymentDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        Integer total = dto.getWorkExperienceTotal();
        Integer current = dto.getWorkExperienceCurrent();

        if (total == null || current == null) return true;

        return current <= total;
    }
}
