package ru.creditservices.calculator.service.api;

import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;

import java.util.List;

public interface ILoanService {
    List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request);
}
