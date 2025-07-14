package ru.creditservices.statement.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.creditservices.statement.dto.ErrorResponseDto;
import ru.creditservices.statement.model.enums.DealClientErrorType;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler(objectMapper);
    }

    @Test
    void handleValidationReturnsBadRequest() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "field1", "msg1"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().getDetails()).containsEntry("field1", "msg1");
    }

    @Test
    void handleConstraintReturnsBadRequest() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("field2");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ErrorResponseDto> response = handler.handleConstraint(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().getDetails()).containsEntry("field2", "must not be null");
    }


    @Test
    void handlePrescoringBusinessReturnsUnprocessable() {
        PrescoringBusinessException ex = new PrescoringBusinessException("some business error");
        ResponseEntity<ErrorResponseDto> response = handler.handlePrescoringBusiness(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("BUSINESS_VALIDATION");
        assertThat(response.getBody().getMessage()).contains("some business error");
    }

    @Test
    void handleRequiredNullReturnsBadRequest() {
        RequiredArgumentIsNullException ex = new RequiredArgumentIsNullException("arg required");
        ResponseEntity<ErrorResponseDto> response = handler.handleRequiredNull(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("REQUIRED_ARGUMENT_NULL");
        assertThat(response.getBody().getMessage()).contains("arg required");
    }

    @Test
    void handleHttpMessageNotReadableReturnsJsonErrorForUuid() {
        Throwable cause = new IllegalArgumentException("Cannot deserialize value of type `java.util.UUID`");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("json error", cause, null);
        ResponseEntity<ErrorResponseDto> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("JSON_PARSE_ERROR");
        assertThat(response.getBody().getMessage()).contains("UUID");
        assertThat(response.getBody().getDetails()).containsKey("reason");
    }

    @Test
    void handleInvalidArgumentReturnsBadRequest() {
        InvalidArgumentException ex = new InvalidArgumentException("invalid arg");
        ResponseEntity<ErrorResponseDto> response = handler.handleInvalidArgument(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("INVALID_ARGUMENT");
        assertThat(response.getBody().getMessage()).isEqualTo("invalid arg");
    }

    @Test
    void handleDealClientParsesNestedJson() throws JsonProcessingException {
        ErrorResponseDto remoteDto = new ErrorResponseDto("BAD_REQUEST", "Remote error",
                Collections.singletonMap("remote", "detail"));
        String nestedJson = "{\"code\":\"BAD_REQUEST\",\"message\":\"Remote error\",\"details\":{\"remote\":\"detail\"}}";
        when(objectMapper.readValue(eq(nestedJson), eq(ErrorResponseDto.class))).thenReturn(remoteDto);

        DealClientException ex = new DealClientException(DealClientErrorType.REQUEST_ERROR, nestedJson);

        ResponseEntity<ErrorResponseDto> response = handler.handleDealClient(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().getMessage()).isEqualTo("Remote error");
        assertThat(response.getBody().getDetails()).containsEntry("remote", "detail");
    }

    @Test
    void handleDealClientHandlesInvalidJson() throws JsonProcessingException {
        String invalidJson = "{invalid}";
        when(objectMapper.readValue(eq(invalidJson), eq(ErrorResponseDto.class)))
                .thenThrow(new JsonProcessingException("json error") {});

        DealClientException ex = new DealClientException(DealClientErrorType.REQUEST_ERROR, invalidJson);

        ResponseEntity<ErrorResponseDto> response = handler.handleDealClient(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("REQUEST_ERROR");
        assertThat(response.getBody().getDetails()).containsKey("error");
    }

    @Test
    void handleOtherReturnsInternalError() {
        RuntimeException ex = new RuntimeException("fail");
        ResponseEntity<ErrorResponseDto> response = handler.handleOther(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().getMessage()).contains("Внутренняя ошибка");
    }
}
