package ru.creditservices.deal.exception;

import lombok.Getter;
import ru.creditservices.deal.dto.Violation;
import ru.creditservices.deal.model.enums.CalculatorErrorType;
import java.util.List;

@Getter
public class CalculatorValidationException extends RuntimeException {
    private final CalculatorErrorType errorType;
    private final List<Violation> violations;

    public CalculatorValidationException(CalculatorErrorType errorType, List<Violation> violations) {
        super(violations != null && !violations.isEmpty()
                ? violations.getFirst().getMessage()
                : "Ошибка калькулятора");
        this.errorType = errorType;
        this.violations = violations;
    }
}
