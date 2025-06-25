package ru.creditservices.deal.exception;

public class CreditAlreadyExistException extends RuntimeException {
    public CreditAlreadyExistException(String message) {
        super(message);
    }
}
