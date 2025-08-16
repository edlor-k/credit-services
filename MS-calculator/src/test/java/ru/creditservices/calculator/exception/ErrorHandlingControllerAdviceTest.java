package ru.creditservices.calculator.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.creditservices.calculator.dto.ErrorResponseDto;
import ru.creditservices.calculator.model.enums.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ErrorHandlingControllerAdviceTest {

    @Test
    void onConstraintViolationException_shouldReturnErrorResponseDto() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("amount");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be positive");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        ErrorHandlingControllerAdvice advice = new ErrorHandlingControllerAdvice();

        ResponseEntity<ErrorResponseDto> response = advice.onConstraintViolationException(ex);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.INVALID_ARGUMENT, body.getCode());
        assertEquals("Ошибка валидации запроса", body.getMessage());
        assertNotNull(body.getDetails());
        assertEquals("must be positive", body.getDetails().get("amount"));

        assertEquals(1, body.getDetails().size());
    }

    @Test
    void onMethodArgumentNotValidException_shouldReturnErrorResponseDto() {
        FieldError fieldError = new FieldError("objectName", "term", "must be greater than 6");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorHandlingControllerAdvice advice = new ErrorHandlingControllerAdvice();

        ResponseEntity<ErrorResponseDto> response = advice.onMethodArgumentNotValidException(ex);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.INVALID_ARGUMENT, body.getCode());
        assertEquals("Ошибка валидации аргументов", body.getMessage());
        Map<String, String> details = body.getDetails();
        assertNotNull(details);
        assertEquals("must be greater than 6", details.get("term"));
        assertEquals(1, details.size());
    }
}
