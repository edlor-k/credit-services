package ru.creditservices.calculator.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkStatusValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkStatus {
    String message() default "Для получения кредита клиент должен быть трудоустроен";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
