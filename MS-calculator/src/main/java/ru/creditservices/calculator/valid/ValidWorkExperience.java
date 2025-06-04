package ru.creditservices.calculator.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkExperienceValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkExperience {
    String message() default "Стаж на текущем месте не может превышать общий стаж";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
