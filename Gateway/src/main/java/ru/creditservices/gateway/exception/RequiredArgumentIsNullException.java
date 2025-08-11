package ru.creditservices.gateway.exception;

public class RequiredArgumentIsNullException extends RuntimeException {
    public RequiredArgumentIsNullException(String message) {
        super(message);
    }
}
