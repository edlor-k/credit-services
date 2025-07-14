package ru.creditservices.statement.service;

import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;

import java.util.List;

public interface DealClientService {
    List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto dto);
    void selectLoanOffer(LoanOfferDto loanOfferDto);
}
