package ru.creditservices.deal.exception;

import lombok.Getter;
import ru.creditservices.deal.dto.ErrorResponseDto;

@Getter
public class CalculatorServiceException extends RuntimeException {
    private final ErrorResponseDto error;

    public CalculatorServiceException(ErrorResponseDto error) {
        super(error != null ? error.getMessage() : "Calculator error");
        this.error = error;
    }
}
