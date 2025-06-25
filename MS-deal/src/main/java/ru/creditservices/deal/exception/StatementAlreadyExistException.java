package ru.creditservices.deal.exception;

public class StatementAlreadyExistException extends RuntimeException {
    public StatementAlreadyExistException(String message) {
        super(message);
    }
}
