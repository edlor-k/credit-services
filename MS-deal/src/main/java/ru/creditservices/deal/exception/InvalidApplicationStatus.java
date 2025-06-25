package ru.creditservices.deal.exception;

public class InvalidApplicationStatus extends RuntimeException {
    public InvalidApplicationStatus(String message) {
        super(message);
    }
}
