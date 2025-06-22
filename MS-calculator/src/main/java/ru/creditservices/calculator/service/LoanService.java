package ru.creditservices.calculator.service;

import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;

import java.util.List;

public interface LoanService {
    List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request);
}
