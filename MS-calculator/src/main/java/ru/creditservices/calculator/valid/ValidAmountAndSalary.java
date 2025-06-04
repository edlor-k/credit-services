package ru.creditservices.calculator.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {AmountAndSalaryValidator.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAmountAndSalary {
    String message() default "Запрашиваемая сумма кредита не должна превышать 24 зарплаты";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
