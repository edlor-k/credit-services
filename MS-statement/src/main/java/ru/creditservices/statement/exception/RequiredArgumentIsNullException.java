package ru.creditservices.statement.exception;

public class RequiredArgumentIsNullException extends RuntimeException {
    public RequiredArgumentIsNullException(String message) {
        super(message);
    }
}
