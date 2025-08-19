package ru.creditservices.calculator.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.creditservices.calculator.dto.ErrorResponseDto;
import ru.creditservices.calculator.model.enums.ErrorCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> onConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> details = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        return ResponseEntity.badRequest().body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.INVALID_ARGUMENT)
                        .message("Ошибка валидации запроса")
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Objects.requireNonNullElse(fe.getDefaultMessage(), "Validation error"),
                        (v1, v2) -> v1
                ));

        return ResponseEntity.badRequest().body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.INVALID_ARGUMENT)
                        .message("Ошибка валидации аргументов")
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleEnumErrors(HttpMessageNotReadableException ex) {
        Map<String, String> details = new HashMap<>();
        String message = "Ошибка в формате запроса";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (!ife.getPath().isEmpty()) {
                String fieldName = ife.getPath().getFirst().getFieldName();
                Class<?> targetType = ife.getTargetType();

                if (targetType.isEnum()) {
                    String allowed = String.join(", ",
                            Arrays.stream(targetType.getEnumConstants())
                                    .map(Object::toString)
                                    .toList()
                    );
                    String value = String.valueOf(ife.getValue());

                    message = String.format(
                            "Недопустимое значение '%s' для поля '%s'. Допустимые значения: %s",
                            value, fieldName, allowed
                    );
                    details.put(fieldName, message);
                }
            }
        }

        return ResponseEntity.badRequest().body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.JSON_PARSE_ERROR)
                        .message(message)
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> onBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.CLIENT_ERROR)
                        .message(ex.getMessage())
                        .details(Map.of("business", ex.getMessage()))
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> onGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.INTERNAL_ERROR)
                        .message("Внутренняя ошибка сервера")
                        .details(Map.of("exception", ex.getClass().getSimpleName()))
                        .build()
        );
    }
}
