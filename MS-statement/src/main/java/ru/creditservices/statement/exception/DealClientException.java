package ru.creditservices.statement.exception;

import lombok.Getter;
import ru.creditservices.statement.model.enums.DealClientErrorType;

@Getter
public class DealClientException extends RuntimeException {
    private final DealClientErrorType errorType;
    private final String details;

    public DealClientException(DealClientErrorType errorType, String message) {
        super(message != null && !message.isEmpty()
                ? message
                : "Ошибка клиента МС Сделка");
        this.errorType = errorType;
        this.details = message;
    }
}
