package ru.creditservices.deal.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.creditservices.deal.model.enums.CalculatorErrorType;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if ("NotNull".equals(error.getCode()) || "NotBlank".equals(error.getCode())) {
                errors.put(
                        error.getField(),
                        "Отсутствует обязательный параметр: " + error.getField()
                );
            } else {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Ошибка валидации входных данных")
                .details(errors)
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(CalculatorValidationException.class)
    public ResponseEntity<ErrorResponse> handleCalculatorValidationException(CalculatorValidationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getErrorType().name())
                .message("Ошибка калькулятора")
                .details(Map.of(
                        "violations", ex.getViolations() != null
                                ? ex.getViolations().toString()
                                : "нет деталей"
                ))
                .build();

        HttpStatus status = ex.getErrorType() == CalculatorErrorType.BUSINESS_ERROR
                ? HttpStatus.UNPROCESSABLE_ENTITY
                : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ParseCalculatorException.class)
    public ResponseEntity<ErrorResponse> handleParseCalculatorException(ParseCalculatorException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("PARSING_ERROR")
                .message("Ошибка интеграции с калькулятором")
                .details(Map.of("error", ex.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Внутренняя ошибка сервиса")
                .details(Map.of("error", ex.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
        private Map<String, String> details;
    }
}
