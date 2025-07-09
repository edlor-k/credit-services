package ru.creditservices.deal.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Некорректный тип параметра запроса")
                .details(Map.of(
                        "Параметр", ex.getName(),
                        "Значение", String.valueOf(ex.getValue())))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if ("NotNull".equals(error.getCode()) || "NotBlank".equals(error.getCode())) {
                errors.put(error.getField(), "Обязательное поле: " + error.getField());
            } else {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Ошибка валидации входных данных")
                .details(errors)
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(ClientNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Клиент не найден")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ClientAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleClientAlreadyExist(ClientAlreadyExistException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Клиент уже существует")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(StatementAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleStatementAlreadyExist(StatementAlreadyExistException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Заявление уже существует")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(StatementNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatementNotFound(StatementNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Заявление не найдено")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(CreditAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleCreditAlreadyExist(CreditAlreadyExistException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Кредит уже существует")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(LoanOfferAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleLoanOfferAlreadyExist(LoanOfferAlreadyExist ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Кредитное предложение уже существует")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidApplicationStatus.class)
    public ResponseEntity<ErrorResponse> handleInvalidApplicationStatus(InvalidApplicationStatus ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Некорректный статус заявки")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        String raw = ex.getMostSpecificCause().getMessage();

        String message = "Некорректный формат JSON. Проверьте структуру запроса.";
        Map<String, String> details = new HashMap<>();

        if (raw != null && raw.contains("not one of the values accepted for Enum class")) {
            String field = null;
            String allowed = null;

            int idxEnum = raw.indexOf("not one of the values accepted for Enum class:");
            if (idxEnum != -1) {
                int start = raw.indexOf('[', idxEnum);
                int end = raw.indexOf(']', idxEnum);
                if (start != -1 && end != -1 && end > start) {
                    allowed = raw.substring(start + 1, end);
                }
            }

            String ref = "through reference chain: ";
            int idxRef = raw.indexOf(ref);
            if (idxRef != -1) {
                String[] refs = raw.substring(idxRef + ref.length()).split("[\\[\\]\"]+");
                for (String r : refs) {
                    if (!r.trim().isEmpty()) {
                        field = r;
                        break;
                    }
                }
            }

            message = "Недопустимое значение поля";
            if (field != null && allowed != null) {
                details.put("Поле", field);
                details.put("Допустимые значения", allowed);
            } else if (field != null) {
                details.put("Поле", field);
            } else {
                details.put("Ошибка", "Некорректное значение для поля с перечислением (enum)");
            }
        } else {
            details.put("Ошибка", raw != null ? raw.split("\n")[0] : "Не удалось прочитать JSON");
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message(message)
                .details(details)
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(CalculatorValidationException.class)
    public ResponseEntity<ErrorResponse> handleCalculatorValidationException(CalculatorValidationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Ошибка бизнес-валидации калькулятора")
                .details(Map.of(
                        "Тип ошибки", ex.getErrorType() != null ? ex.getErrorType().name() : "Не указано",
                        "Нарушения", ex.getViolations() != null ? ex.getViolations().toString() : "Нет деталей"
                ))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ParseCalculatorException.class)
    public ResponseEntity<ErrorResponse> handleParseCalculatorException(ParseCalculatorException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message("Ошибка интеграции с калькулятором")
                .details(Map.of("Причина", ex.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.name())
                .message("Ресурс не найден")
                .details(Map.of("Путь", ex.getRequestURL()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message("Внутренняя ошибка сервиса")
                .details(Map.of("Ошибка", ex.getMessage()))
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
