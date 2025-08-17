package ru.creditservices.statement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.statement.dto.ErrorResponseDto;
import ru.creditservices.statement.model.enums.ErrorCode;
import ru.creditservices.statement.service.DealErrorHandler;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealErrorHandlerImpl implements DealErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public ErrorResponseDto parseErrorOrFallback(String raw,
                                                 ErrorCode fallbackCode,
                                                 String fallbackMsg,
                                                 String endpoint) {
        if (raw != null && !raw.isBlank()) {
            try {
                return objectMapper.readValue(raw, ErrorResponseDto.class);
            } catch (Exception e) {
                log.error("Failed to parse Deal error body at {}: {}", endpoint, raw, e);
                return buildError(ErrorCode.JSON_PARSE_ERROR,
                        "Некорректный формат ошибки Deal", endpoint);
            }
        }
        return buildError(fallbackCode, fallbackMsg, endpoint);
    }

    @Override
    public void logHttpError(RestClientResponseException ex, String endpoint) {
        String raw = ex.getResponseBodyAsString();
        log.warn("Deal HTTP error at {}: status={} {}, bodyPresent={}",
                endpoint,
                ex.getStatusCode().value(),
                HttpStatus.resolve(ex.getStatusCode().value()),
                !raw.isBlank());
    }

    @Override
    public ErrorResponseDto buildError(ErrorCode code, String message, String endpoint) {
        return ErrorResponseDto.builder()
                .code(code)
                .message(message)
                .details(Map.of("endpoint", endpoint))
                .build();
    }
}
