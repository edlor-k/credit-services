package ru.creditservices.deal.service;

import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;

import java.util.List;

public interface CreateLoanStatementService {
    List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto);
}
