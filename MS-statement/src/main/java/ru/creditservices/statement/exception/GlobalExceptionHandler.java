package ru.creditservices.statement.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.creditservices.statement.dto.ErrorResponseDto;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorResponseDto response = new ErrorResponseDto(
                "BAD_REQUEST",
                "Ошибка валидации входных параметров.",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                details.put(v.getPropertyPath().toString(), v.getMessage()));
        ErrorResponseDto response = new ErrorResponseDto(
                "BAD_REQUEST",
                "Ошибка валидации запроса.",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PrescoringBusinessException.class)
    public ResponseEntity<ErrorResponseDto> handlePrescoringBusiness(PrescoringBusinessException ex) {
        ErrorResponseDto response = new ErrorResponseDto(
                "BUSINESS_VALIDATION",
                ex.getMessage(),
                ex.getDetails()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(RequiredArgumentIsNullException.class)
    public ResponseEntity<ErrorResponseDto> handleRequiredNull(RequiredArgumentIsNullException ex) {
        ErrorResponseDto response = new ErrorResponseDto(
                "REQUIRED_ARGUMENT_NULL",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Ошибка чтения запроса: некорректный формат JSON.";
        Map<String, String> details = new HashMap<>();

        Throwable cause = ex.getMostSpecificCause();
        String rawMessage = cause != null ? cause.getMessage() : ex.getMessage();
        if (rawMessage != null && rawMessage.contains("UUID")) {
            message = "Некорректный формат UUID. UUID должен быть в стандартном 36-символьном формате.";
        }
        details.put("reason", rawMessage != null ? rawMessage : ex.getMessage());

        ErrorResponseDto response = new ErrorResponseDto(
                "JSON_PARSE_ERROR",
                message,
                details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidArgument(InvalidArgumentException ex) {
        ErrorResponseDto response = new ErrorResponseDto(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DealClientException.class)
    public ResponseEntity<ErrorResponseDto> handleDealClient(DealClientException ex) {
        String code = ex.getErrorType().name();
        String message = ex.getDetails();
        Map<String, String> details = null;

        if (message != null && message.trim().startsWith("{")) {
            try {
                ErrorResponseDto remote = objectMapper.readValue(message, ErrorResponseDto.class);
                code = remote.getCode() != null ? remote.getCode() : code;
                message = remote.getMessage();
                details = remote.getDetails();
            } catch (Exception parseEx) {
                log.error("Failed to parse nested JSON in DealClientException", parseEx);
                details = new HashMap<>();
                details.put("error", "Не удалось распарсить вложенный JSON: " + parseEx.getMessage());
            }
        }
        ErrorResponseDto response = new ErrorResponseDto(
                code,
                message,
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponseDto response = new ErrorResponseDto(
                "INTERNAL_ERROR",
                "Внутренняя ошибка сервиса.",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
