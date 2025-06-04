package ru.creditservices.calculator.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CorrectAgeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Клиент должен быть в возрасте от 20 до 65 лет";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
