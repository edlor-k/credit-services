package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.CalculatorResult;
import ru.creditservices.deal.dto.LoanOfferDto;

import java.util.List;

public interface ErrorResponseParserService {
    List<LoanOfferDto> parseLoanOffersResponse(String response);

    CalculatorResult parseCalculatorResultResponse(String response);
}
