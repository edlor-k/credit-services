package ru.creditservices.statement.service;

import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;

import java.util.List;

public interface GetLoanOffersService {
    List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request);
}
