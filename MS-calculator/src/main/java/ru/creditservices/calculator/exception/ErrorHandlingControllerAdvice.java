package ru.creditservices.calculator.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.creditservices.calculator.dto.ValidationErrorResponse;
import ru.creditservices.calculator.dto.Violation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> onConstraintViolationException(
            ConstraintViolationException e) {

        List<Violation> violations = e.getConstraintViolations().stream()
                .map(v -> new Violation(
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ValidationErrorResponse(violations), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ValidationErrorResponse(violations), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleEnumErrors(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (!ife.getPath().isEmpty()) {
                String fieldName = ife.getPath().getFirst().getFieldName();
                Class<?> targetType = ife.getTargetType();

                if (targetType.isEnum()) {
                    String allowed = Arrays.stream(targetType.getEnumConstants())
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                    String value = String.valueOf(ife.getValue());

                    String message = String.format(
                            "Недопустимое значение '%s' для поля '%s'. Допустимые значения: %s",
                            value, fieldName, allowed
                    );
                    return ResponseEntity
                            .badRequest()
                            .body(new ValidationErrorResponse(List.of(new Violation(fieldName, message))));
                }
            }
        }
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorResponse(List.of(new Violation("request",
                        "Ошибка в формате запроса"))));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ValidationErrorResponse> onBusinessException(BusinessException ex) {
        Violation violation = new Violation("business", ex.getMessage());
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(List.of(violation)));
    }

}
