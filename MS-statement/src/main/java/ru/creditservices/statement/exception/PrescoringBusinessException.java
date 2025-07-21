package ru.creditservices.statement.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class PrescoringBusinessException extends RuntimeException {

    private final Map<String, String> details;

    public PrescoringBusinessException(String message) {
        super(message);
        this.details = null;
    }

    public PrescoringBusinessException(String message, Map<String, String> details) {
        super(message);
        this.details = details;
    }
}
