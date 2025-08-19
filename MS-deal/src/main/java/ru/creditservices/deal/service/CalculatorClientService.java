package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.CreditDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.ScoringDataDto;

import java.util.List;

public interface CalculatorClientService {
    List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto);
    CreditDto fetchCalculatorResult(ScoringDataDto dto);
}
