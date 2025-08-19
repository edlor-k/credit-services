package ru.creditservices.statement.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.creditservices.statement.dto.ErrorResponseDto;
import ru.creditservices.statement.model.enums.ErrorCode;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationReturnsBadRequest() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "field1", "msg1"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
        assertThat(response.getBody().getDetails()).containsEntry("field1", "msg1");
    }

    @Test
    void handleConstraintReturnsBadRequest() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("field2");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ErrorResponseDto> response = handler.handleConstraint(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.BAD_REQUEST);
        assertThat(response.getBody().getDetails()).containsEntry("field2", "must not be null");
    }

    @Test
    void handlePrescoringBusinessReturnsUnprocessable() {
        PrescoringBusinessException ex = new PrescoringBusinessException("some business error");

        ResponseEntity<ErrorResponseDto> response = handler.handlePrescoringBusiness(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.CLIENT_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("some business error");
    }

    @Test
    void handleRequiredNullReturnsBadRequest() {
        RequiredArgumentIsNullException ex = new RequiredArgumentIsNullException("arg required");

        ResponseEntity<ErrorResponseDto> response = handler.handleRequiredNull(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.REQUIRED_ARGUMENT_NULL);
        assertThat(response.getBody().getMessage()).isEqualTo("arg required");
    }

    @Test
    void handleHttpMessageNotReadableReturnsUuidError() {
        Throwable cause = new IllegalArgumentException("Cannot deserialize value of type `java.util.UUID`");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("json error", cause, null);

        ResponseEntity<ErrorResponseDto> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.JSON_PARSE_ERROR);
        assertThat(response.getBody().getMessage()).contains("UUID");
        assertThat(response.getBody().getDetails()).containsKey("reason");
    }

    @Test
    void handleHttpMessageNotReadableReturnsGenericJsonError() {
        Throwable cause = new IllegalArgumentException("Some other parse error");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("json error", cause, null);

        ResponseEntity<ErrorResponseDto> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.JSON_PARSE_ERROR);
        assertThat(response.getBody().getMessage()).contains("Ошибка чтения запроса");
        assertThat(response.getBody().getDetails()).containsEntry("reason", "Some other parse error");
    }

    @Test
    void handleInvalidArgumentReturnsBadRequest() {
        InvalidArgumentException ex = new InvalidArgumentException("invalid arg");

        ResponseEntity<ErrorResponseDto> response = handler.handleInvalidArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INVALID_ARGUMENT);
        assertThat(response.getBody().getMessage()).isEqualTo("invalid arg");
    }

    @Test
    void handleDealServiceReturnsCustomStatus() {
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .code(ErrorCode.BAD_REQUEST)
                .message("deal error")
                .build();

        DealServiceException ex = new DealServiceException(errorDto, HttpStatus.SERVICE_UNAVAILABLE);

        ResponseEntity<ErrorResponseDto> response = handler.handleDealService(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isEqualTo(errorDto);
    }

    @Test
    void handleOtherReturnsInternalError() {
        Exception ex = new Exception("fail");

        ResponseEntity<ErrorResponseDto> response = handler.handleOther(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
        assertThat(response.getBody().getMessage()).contains("Внутренняя ошибка");
    }
}
