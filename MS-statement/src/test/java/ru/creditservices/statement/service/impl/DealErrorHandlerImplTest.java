package ru.creditservices.statement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.statement.dto.ErrorResponseDto;
import ru.creditservices.statement.model.enums.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DealErrorHandlerImplTest {

    private ObjectMapper objectMapper;
    private DealErrorHandlerImpl errorHandler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        errorHandler = new DealErrorHandlerImpl(objectMapper);
    }

    @Test
    void parseErrorOrFallback_returnsParsedError_whenValidJson() throws Exception {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .code(ErrorCode.CLIENT_ERROR)
                .message("Invalid input")
                .details(Map.of("endpoint", "/statement"))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        ErrorResponseDto result = errorHandler.parseErrorOrFallback(
                json, ErrorCode.INTERNAL_ERROR, "fallback", "/statement");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void parseErrorOrFallback_returnsJsonParseError_whenInvalidJson() {
        String invalidJson = "{ this is not valid json }";

        ErrorResponseDto result = errorHandler.parseErrorOrFallback(
                invalidJson, ErrorCode.INTERNAL_ERROR, "fallback", "/statement");

        assertThat(result.getCode()).isEqualTo(ErrorCode.JSON_PARSE_ERROR);
        assertThat(result.getMessage()).contains("Некорректный формат ошибки Deal");
        assertThat(result.getDetails()).containsEntry("endpoint", "/statement");
    }

    @Test
    void parseErrorOrFallback_returnsFallback_whenEmptyString() {
        ErrorResponseDto result = errorHandler.parseErrorOrFallback(
                "   ", ErrorCode.INTERNAL_ERROR, "fallback", "/offer");

        assertThat(result.getCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
        assertThat(result.getMessage()).isEqualTo("fallback");
        assertThat(result.getDetails()).containsEntry("endpoint", "/offer");
    }

    @Test
    void buildError_returnsExpectedDto() {
        ErrorResponseDto result = errorHandler.buildError(
                ErrorCode.CLIENT_ERROR, "Bad request", "/statement");

        assertThat(result.getCode()).isEqualTo(ErrorCode.CLIENT_ERROR);
        assertThat(result.getMessage()).isEqualTo("Bad request");
        assertThat(result.getDetails()).containsEntry("endpoint", "/statement");
    }

    @Test
    void logHttpError_logsWarning() {
        RestClientResponseException ex = new RestClientResponseException(
                "Bad request", HttpStatus.BAD_REQUEST.value(),
                "Bad Request", null,
                "error body".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        errorHandler.logHttpError(ex, "/statement");

        assertThat(ex.getResponseBodyAsString()).isEqualTo("error body");
    }
}
