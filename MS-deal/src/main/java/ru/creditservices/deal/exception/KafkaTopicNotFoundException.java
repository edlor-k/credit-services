package ru.creditservices.deal.exception;

public class KafkaTopicNotFoundException extends RuntimeException {
    public KafkaTopicNotFoundException(String message) {
        super(message);
    }
}
