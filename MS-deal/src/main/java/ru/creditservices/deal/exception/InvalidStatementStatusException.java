package ru.creditservices.deal.exception;

public class InvalidStatementStatusException extends RuntimeException {
    public InvalidStatementStatusException(String message) {
        super(message);
    }
}
