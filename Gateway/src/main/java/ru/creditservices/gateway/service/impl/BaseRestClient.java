package ru.creditservices.gateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class BaseRestClient {

    protected <T> T execute(Supplier<T> action,
                            String context,
                            Function<String, ? extends RuntimeException> exceptionMapper) {
        try {
            log.info("Начало вызова внешнего сервиса: {}", context);
            T result = action.get();
            log.info("Успешный ответ от внешнего сервиса: {}", context);
            return result;
        } catch (HttpClientErrorException ex) {
            String responseBody = ex.getResponseBodyAsString();
            log.warn("HTTP ошибка от внешнего сервиса при {} ({}): {}",
                    context, ex.getStatusCode(), responseBody);
            throw exceptionMapper.apply(responseBody);
        } catch (RestClientException ex) {
            log.error("Внешний сервис недоступен или вернул ошибку при {}: {}",
                    context, ex.getMessage());
            throw exceptionMapper.apply("Сервис недоступен или вернул пустой ответ");
        }
    }
}
