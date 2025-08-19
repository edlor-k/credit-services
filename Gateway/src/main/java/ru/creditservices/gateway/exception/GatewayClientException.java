package ru.creditservices.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.creditservices.gateway.dto.ErrorResponseDto;

@Getter
public class GatewayClientException extends RuntimeException {

    private final ErrorResponseDto errorResponse;
    private final HttpStatus httpStatus;

    public GatewayClientException(ErrorResponseDto errorResponse, HttpStatus httpStatus) {
        super(errorResponse != null ? errorResponse.getMessage() : null);
        this.errorResponse = errorResponse;
        this.httpStatus = httpStatus;
    }
}
