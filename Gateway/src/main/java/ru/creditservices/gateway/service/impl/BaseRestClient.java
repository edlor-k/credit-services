package ru.creditservices.gateway.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import ru.creditservices.gateway.dto.ErrorResponseDto;
import ru.creditservices.gateway.exception.GatewayClientException;
import ru.creditservices.gateway.model.enums.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public abstract class BaseRestClient {

    @Autowired
    private ObjectMapper objectMapper;

    protected <T> T execute(Supplier<T> action, String context) {
        try {
            log.info("Начало вызова внешнего сервиса: {}", context);
            T result = action.get();
            log.info("Успешный ответ от внешнего сервиса: {}", context);
            return result;

        } catch (RestClientResponseException ex) {
            String body = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();
            HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
            if (httpStatus == null) httpStatus = HttpStatus.BAD_REQUEST;

            log.warn("HTTP ошибка от внешнего сервиса при {} ({} {}): {}",
                    context, statusCode.value(), ex.getStatusText(), body);

            ErrorResponseDto parsed = tryParse(body);
            if (parsed != null) {
                throw new GatewayClientException(
                        parsed.getCode() != null ? parsed.getCode() : ErrorCode.CLIENT_ERROR,
                        parsed.getMessage() != null ? parsed.getMessage() : "Внешний сервис вернул ошибку",
                        parsed.getDetails(),
                        httpStatus
                );
            }

            throw new GatewayClientException(
                    ErrorCode.CLIENT_ERROR,
                    "Внешний сервис вернул ошибку",
                    Map.of(
                            "context", context,
                            "httpStatus", String.valueOf(statusCode.value()),
                            "statusText", ex.getStatusText() == null ? "" : ex.getStatusText()
                    ),
                    httpStatus
            );

        } catch (RestClientException ex) {
            log.error("Внешний сервис недоступен при {}: {}", context, ex.getMessage());
            throw new GatewayClientException(
                    ErrorCode.CLIENT_ERROR,
                    "Внешний сервис недоступен",
                    Map.of(
                            "context", context,
                            "reason", ex.getMessage() == null ? "" : ex.getMessage()
                    ),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    private ErrorResponseDto tryParse(String body) {
        if (body == null) return null;
        String trimmed = body.trim();
        if (!trimmed.startsWith("{")) return null;
        try {
            return objectMapper.readValue(trimmed, ErrorResponseDto.class);
        } catch (Exception ignore) {
            return null;
        }
    }
}
