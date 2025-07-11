package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.CalculatorResult;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.dto.ScoringDataDto;

import java.util.List;

public interface CalculatorClientService {
    List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto);
    CalculatorResult fetchCalculatorResult(ScoringDataDto dto);
}
