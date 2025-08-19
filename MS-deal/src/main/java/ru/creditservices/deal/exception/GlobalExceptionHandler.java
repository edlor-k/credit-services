package ru.creditservices.deal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.creditservices.deal.dto.ErrorResponseDto;
import ru.creditservices.deal.model.enums.ErrorCode;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT,
                "Некорректный тип параметра запроса",
                Map.of("parameter", ex.getName(), "value", String.valueOf(ex.getValue())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if ("NotNull".equals(error.getCode()) || "NotBlank".equals(error.getCode())) {
                errors.put(error.getField(), "Обязательное поле: " + error.getField());
            } else {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT,
                "Ошибка валидации входных данных", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidJson(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String raw = cause.getMessage();

        Map<String, String> details = new HashMap<>();
        details.put("error", raw != null ? raw.split("\n")[0] : "Не удалось прочитать JSON");

        return build(HttpStatus.BAD_REQUEST, ErrorCode.JSON_PARSE_ERROR,
                "Некорректный формат JSON. Проверьте структуру запроса.", details);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleClientNotFound(ClientNotFoundException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Клиент не найден", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(ClientAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleClientAlreadyExist(ClientAlreadyExistException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Клиент уже существует", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(StatementAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleStatementAlreadyExist(StatementAlreadyExistException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Заявление уже существует", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(StatementNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleStatementNotFound(StatementNotFoundException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Заявление не найдено", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(CreditAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleCreditAlreadyExist(CreditAlreadyExistException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Кредит уже существует", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(LoanOfferAlreadyExist.class)
    public ResponseEntity<ErrorResponseDto> handleLoanOfferAlreadyExist(LoanOfferAlreadyExist ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Кредитное предложение уже существует", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(InvalidApplicationStatus.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidApplicationStatus(InvalidApplicationStatus ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Некорректный статус заявки", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(InvalidStatementStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidStatementStatus(InvalidStatementStatusException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Передан некорректный статус заявления", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(InvalidSesCodeException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidSesCode(InvalidSesCodeException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Передан некорректный код подтверждения", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(SesCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleSesCodeNotFound(SesCodeNotFoundException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "У заявки не обнаружен ses-код", Map.of("reason", ex.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NoHandlerFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ErrorCode.BAD_REQUEST,
                "Ресурс не найден", Map.of("path", ex.getRequestURL()));
    }

    @ExceptionHandler(CalculatorServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleCalculatorService(CalculatorServiceException ex) {
        ErrorResponseDto err = ex.getError();
        HttpStatus status = mapErrorCodeToStatus(err != null ? err.getCode() : null);
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ParseCalculatorException.class)
    public ResponseEntity<ErrorResponseDto> handleParseCalculator(ParseCalculatorException ex) {
        ErrorResponseDto err = ErrorResponseDto.builder()
                .code(ErrorCode.JSON_PARSE_ERROR)
                .message(ex.getMessage())
                .details(Map.of("source", "calculator"))
                .build();
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "Внутренняя ошибка сервиса", Map.of("exception", ex.getClass().getSimpleName()));
    }

    private ResponseEntity<ErrorResponseDto> build(HttpStatus status,
                                                   ErrorCode code,
                                                   String message,
                                                   Map<String, String> details) {
        ErrorResponseDto body = ErrorResponseDto.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private HttpStatus mapErrorCodeToStatus(ErrorCode code) {
        if (code == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        return switch (code) {
            case INVALID_ARGUMENT, REQUIRED_ARGUMENT_NULL, BAD_REQUEST, JSON_PARSE_ERROR -> HttpStatus.BAD_REQUEST;
            case CLIENT_ERROR -> HttpStatus.UNPROCESSABLE_ENTITY; // при желании можно 409
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
