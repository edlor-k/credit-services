package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.LoanOfferDto;

import java.util.List;

public interface ErrorResponseParserService {
    List<LoanOfferDto> parseLoanOffersResponse(String response);
    CreditDto parseCalculatorResultResponse(String response);
}
