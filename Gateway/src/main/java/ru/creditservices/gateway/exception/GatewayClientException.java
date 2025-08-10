package ru.creditservices.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.creditservices.gateway.model.enums.ErrorCode;

import java.util.Map;

@Getter
public class GatewayClientException extends RuntimeException {
    private final ErrorCode code;
    private final String userMessage;
    private final Map<String, String> details;
    private final HttpStatus httpStatus;

    public GatewayClientException(ErrorCode code,
                                  String userMessage,
                                  Map<String, String> details,
                                  HttpStatus httpStatus) {
        super(userMessage);
        this.code = code;
        this.userMessage = userMessage;
        this.details = details;
        this.httpStatus = httpStatus;
    }
}
