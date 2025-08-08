package ru.creditservices.gateway.service;

import ru.creditservices.gateway.dto.LoanOfferDto;
import ru.creditservices.gateway.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementClientService {
    List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto request);
    void selectLoanOffer(LoanOfferDto loanOfferDto);
}
