package ru.creditservices.statement.exception;

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
import ru.creditservices.statement.model.enums.ErrorCode;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.BAD_REQUEST)
                        .message("Ошибка валидации входных параметров.")
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                details.put(v.getPropertyPath().toString(), v.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.BAD_REQUEST)
                        .message("Ошибка валидации запроса.")
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(PrescoringBusinessException.class)
    public ResponseEntity<ErrorResponseDto> handlePrescoringBusiness(PrescoringBusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.CLIENT_ERROR)
                        .message(ex.getMessage())
                        .details(ex.getDetails())
                        .build()
        );
    }

    @ExceptionHandler(RequiredArgumentIsNullException.class)
    public ResponseEntity<ErrorResponseDto> handleRequiredNull(RequiredArgumentIsNullException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.REQUIRED_ARGUMENT_NULL)
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Ошибка чтения запроса: некорректный формат JSON.";
        Map<String, String> details = new HashMap<>();

        Throwable cause = ex.getMostSpecificCause();
        String rawMessage = cause.getMessage();
        if (rawMessage != null && rawMessage.contains("UUID")) {
            message = "Некорректный формат UUID. UUID должен быть в стандартном 36-символьном формате.";
        }
        details.put("reason", rawMessage != null ? rawMessage : ex.getMessage());

        return ResponseEntity.badRequest().body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.JSON_PARSE_ERROR)
                        .message(message)
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidArgument(InvalidArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.INVALID_ARGUMENT)
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DealServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleDealService(DealServiceException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getError());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseDto.builder()
                        .code(ErrorCode.INTERNAL_ERROR)
                        .message("Внутренняя ошибка сервиса.")
                        .build()
        );
    }
}
