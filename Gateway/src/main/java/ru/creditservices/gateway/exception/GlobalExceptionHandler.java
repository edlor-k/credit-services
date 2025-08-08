package ru.creditservices.gateway.exception;

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
import ru.creditservices.gateway.dto.ErrorResponseDto;
import ru.creditservices.gateway.model.enums.ErrorCode;

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
        return buildResponse(ErrorCode.BAD_REQUEST, "Ошибка валидации входных параметров.", details,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                details.put(v.getPropertyPath().toString(), v.getMessage()));
        return buildResponse(ErrorCode.BAD_REQUEST, "Ошибка валидации запроса.", details,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequiredArgumentIsNullException.class)
    public ResponseEntity<ErrorResponseDto> handleRequiredNull(RequiredArgumentIsNullException ex) {
        return buildResponse(ErrorCode.REQUIRED_ARGUMENT_NULL, ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Ошибка чтения запроса: некорректный формат JSON.";
        String rawMessage = ex.getMostSpecificCause() != null ?
                ex.getMostSpecificCause().getMessage() : ex.getMessage();

        if (rawMessage != null && rawMessage.contains("UUID")) {
            message = "Некорректный формат UUID. UUID должен быть в стандартном 36-символьном формате.";
        }

        Map<String, String> details = Map.of("reason", rawMessage);
        return buildResponse(ErrorCode.JSON_PARSE_ERROR, message, details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidArgument(InvalidArgumentException ex) {
        return buildResponse(ErrorCode.INVALID_ARGUMENT, ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DealClientException.class, StatementClientException.class})
    public ResponseEntity<ErrorResponseDto> handleClientExceptions(RuntimeException ex) {
        ErrorCode code = ErrorCode.CLIENT_ERROR;
        String message = ex.getMessage();
        Map<String, String> details = null;

        if (ex instanceof DealClientException dce) {
            code = ErrorCode.valueOf(dce.getErrorType().name());
            message = dce.getDetails();
        } else if (ex instanceof StatementClientException sce) {
            code = ErrorCode.valueOf(sce.getErrorType().name());
            message = sce.getDetails();
        }

        if (message != null && message.trim().startsWith("{")) {
            try {
                ErrorResponseDto parsed = objectMapper.readValue(message, ErrorResponseDto.class);
                code = parsed.getCode() != null ? parsed.getCode() : code;
                message = parsed.getMessage();
                details = parsed.getDetails();
            } catch (Exception parseEx) {
                log.error("Ошибка парсинга JSON в clientException", parseEx);
                details = Map.of("error", "Не удалось распарсить вложенный JSON: " + parseEx.getMessage());
            }
        }

        return buildResponse(code, message, details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex) {
        log.error("Непредвиденная ошибка", ex);
        return buildResponse(ErrorCode.INTERNAL_ERROR, "Внутренняя ошибка сервиса.", null,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(
            ErrorCode code,
            String message,
            Map<String, String> details,
            HttpStatus status
    ) {
        return ResponseEntity.status(status).body(
                ErrorResponseDto.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build()
        );
    }
}

