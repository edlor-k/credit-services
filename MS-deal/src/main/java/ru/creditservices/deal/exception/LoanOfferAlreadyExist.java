package ru.creditservices.deal.exception;

public class LoanOfferAlreadyExist extends RuntimeException {
    public LoanOfferAlreadyExist(String message) {
        super(message);
    }
}
