package ru.creditservices.statement.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementApi {

    ResponseEntity<List<LoanOfferDto>> getLoanOffers(@Valid @RequestBody LoanStatementRequestDto request);

    ResponseEntity<Void> selectLoanOffer(@Valid @RequestBody LoanOfferDto loanOfferDto);
}
