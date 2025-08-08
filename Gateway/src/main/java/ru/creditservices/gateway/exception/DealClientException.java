package ru.creditservices.gateway.exception;

import lombok.Getter;
import ru.creditservices.gateway.model.enums.RestClientErrorType;

@Getter
public class DealClientException extends RuntimeException {
  private final RestClientErrorType errorType;
  private final String details;

  public DealClientException(RestClientErrorType errorType, String message) {
    super(message != null && !message.isEmpty()
            ? message
            : "Ошибка клиента МС Сделка");
    this.errorType = errorType;
    this.details = message;
  }
}
