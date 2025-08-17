package ru.creditservices.statement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.creditservices.statement.dto.ErrorResponseDto;

@Getter
public class DealServiceException extends RuntimeException {
    private final ErrorResponseDto error;
    private final HttpStatus status;

    public DealServiceException(ErrorResponseDto error, HttpStatus status) {
        super(error != null ? error.getMessage() : "Deal service error");
        this.error = error;
        this.status = status;
    }
}
