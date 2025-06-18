package ru.creditservices.calculator.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.creditservices.calculator.dto.ValidationErrorResponse;
import ru.creditservices.calculator.dto.Violation;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ErrorHandlingControllerAdviceTest {

    @Test
    void onConstraintViolationExceptionShouldReturnViolations() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn("amount");
        when(violation.getPropertyPath()).thenReturn(mockPath);

        when(violation.getMessage()).thenReturn("must be positive");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));
        ErrorHandlingControllerAdvice advice = new ErrorHandlingControllerAdvice();

        ResponseEntity<ValidationErrorResponse> response = advice.onConstraintViolationException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        List<Violation> violations = response.getBody().getViolations();
        assertEquals(1, violations.size());
        assertEquals("amount", violations.getFirst().getFieldName());
        assertEquals("must be positive", violations.getFirst().getMessage());
    }

    @Test
    void onMethodArgumentNotValidExceptionShouldReturnViolations() {
        FieldError fieldError = new FieldError
                ("objectName", "term", "must be greater than 6");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodParameter methodParameter = mock(MethodParameter.class);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ErrorHandlingControllerAdvice advice = new ErrorHandlingControllerAdvice();

        var response = advice.onMethodArgumentNotValidException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        List<Violation> violations = response.getBody().getViolations();
        assertEquals(1, violations.size());
        assertEquals("term", violations.getFirst().getFieldName());
        assertEquals("must be greater than 6", violations.getFirst().getMessage());
    }
}
