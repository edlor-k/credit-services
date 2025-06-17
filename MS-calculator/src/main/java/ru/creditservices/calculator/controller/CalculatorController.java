package ru.creditservices.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditservices.calculator.dto.CreditDto;
import ru.creditservices.calculator.dto.LoanOfferDto;
import ru.creditservices.calculator.dto.LoanStatementRequestDto;
import ru.creditservices.calculator.dto.ScoringDataDto;
import ru.creditservices.calculator.service.api.ILoanService;
import ru.creditservices.calculator.service.api.IScoringService;

import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Slf4j
@Tag(name="Calculator", description = "Prescoring and credit calculation API")
public class CalculatorController {

    private final ILoanService loanService;
    private final IScoringService scoringService;

    @PostMapping("/offers")
    @Operation(summary = "Get loan offers",
            description = "Returns a list of 4 loan offers or a rejection based on " +
                    "client data and loan parameters.")
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(
            @Valid @RequestBody LoanStatementRequestDto request) {
        log.info("[CalculatorController] Prescoring input data: {}", request);
        List<LoanOfferDto> offers = loanService.getLoanOffers(request);
        log.info("[CalculatorController] Intermediate result (number of offers): {}", offers.size());
        log.info("[CalculatorController] Generated loan offers: {}", offers);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/calc")
    @Operation(summary = "Credit calculation based on client data",
            description = "Returns credit information including amount, " +
                    "term, monthly payment, and other parameters.")
    public ResponseEntity<CreditDto> calculateCredit(
            @Valid @RequestBody ScoringDataDto request) {
        log.info("[CalculatorController] Credit calculation input data: {}", request);
        CreditDto credit = scoringService.getFinalCreditInfo(request);
        log.info("[CalculatorController] Intermediate result (monthly payment): {}",
                credit.getMonthlyPayment());
        log.info("[CalculatorController] Credit calculation result: {}", credit);
        return ResponseEntity.ok(credit);
    }
}