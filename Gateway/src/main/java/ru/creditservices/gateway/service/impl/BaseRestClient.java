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

            // Если внешка вернула нормальный ErrorResponseDto -> пробрасываем его как есть
            ErrorResponseDto parsed = tryParse(body);
            if (parsed != null) {
                throw new GatewayClientException(parsed, httpStatus);
            }

            // Если не смогли распарсить JSON -> формируем fallback
            throw new GatewayClientException(
                    ErrorResponseDto.builder()
                            .code(ErrorCode.CLIENT_ERROR)
                            .message("Внешний сервис вернул ошибку")
                            .details(Map.of(
                                    "context", context,
                                    "httpStatus", String.valueOf(statusCode.value()),
                                    "statusText", ex.getStatusText() == null ? "" : ex.getStatusText()
                            ))
                            .build(),
                    httpStatus
            );

        } catch (RestClientException ex) {
            log.error("Внешний сервис недоступен при {}: {}", context, ex.getMessage());
            throw new GatewayClientException(
                    ErrorResponseDto.builder()
                            .code(ErrorCode.CLIENT_ERROR)
                            .message("Внешний сервис недоступен")
                            .details(Map.of(
                                    "context", context,
                                    "reason", ex.getMessage() == null ? "" : ex.getMessage()
                            ))
                            .build(),
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
