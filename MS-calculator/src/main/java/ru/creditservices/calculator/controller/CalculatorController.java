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
import ru.creditservices.calculator.service.CalculatorService;

import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Slf4j
@Tag(name="Calculator", description = "Prescoring and credit calculation API")
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    @Operation(summary = "Get loan offers",
            description = "Returns a list of 4 loan offers or a rejection based on client data and loan parameters.")
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for prescoring",
                    required = true
            )
            @Valid @RequestBody LoanStatementRequestDto request) {
        log.info("Prescoring input data: {}", request);
        List<LoanOfferDto> offers = calculatorService.prescoring(request);
        log.info("Intermediate result (number of offers): {}", offers.size());
        log.info("Generated loan offers: {}", offers);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/calc")
    @Operation(summary = "Credit calculation based on client data",
            description = "Returns credit information including amount, term, monthly payment, and other parameters.")
    public ResponseEntity<CreditDto> calculateCredit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Client data for credit calculation",
                    required = true
            )
            @Valid @RequestBody ScoringDataDto request) {
        log.info("Credit calculation input data: {}", request);
        CreditDto credit = calculatorService.calculate(request);
        log.info("Intermediate result (monthly payment): {}", credit.getMonthlyPayment());
        log.info("Credit calculation result: {}", credit);
        return ResponseEntity.ok(credit);
    }
}