package ru.creditservices.statement.service;

import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.statement.dto.ErrorResponseDto;
import ru.creditservices.statement.model.enums.ErrorCode;

public interface DealErrorHandler {
    ErrorResponseDto parseErrorOrFallback(String raw,
                                          ErrorCode fallbackCode,
                                          String fallbackMsg,
                                          String endpoint);

    void logHttpError(RestClientResponseException ex, String endpoint);

    ErrorResponseDto buildError(ErrorCode code, String message, String endpoint);
}
