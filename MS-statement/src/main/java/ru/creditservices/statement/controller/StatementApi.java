package ru.creditservices.statement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.creditservices.statement.dto.LoanOfferDto;
import ru.creditservices.statement.dto.LoanStatementRequestDto;

import java.util.List;

@Tag(name = "Statement API", description = "API for managing statements")
@RequestMapping("/statement")
public interface StatementApi {
    @Operation(summary = "Get loan offers",
        description = "Retrieves a list of available loan offers")
    @PostMapping()
    ResponseEntity<List<LoanOfferDto>> getLoanOffers(@Valid @RequestBody LoanStatementRequestDto request);

    @Operation(summary = "Select loan offer",
        description = "Selects a loan offer from the list of offers and saves it to the statement")
    @PostMapping("/offer")
    ResponseEntity<Void> selectLoanOffer(@Valid @RequestBody LoanOfferDto loanOfferDto);
}
