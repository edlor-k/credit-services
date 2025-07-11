package ru.creditservices.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.creditservices.deal.dto.FinishRegistrationRequestDto;
import ru.creditservices.deal.dto.LoanOfferDto;
import ru.creditservices.deal.dto.LoanStatementRequestDto;
import ru.creditservices.deal.service.CalculateFinalParametersService;
import ru.creditservices.deal.service.CreateLoanStatementService;
import ru.creditservices.deal.service.SelectLoanOfferService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deal Microservice", description = "API for managing loan deals")
public class DealController {

    private final CalculateFinalParametersService calculateFinalParametersService;
    private final CreateLoanStatementService createLoanStatementService;
    private final SelectLoanOfferService selectLoanOfferService;

    @PostMapping("/statement")
    @Operation(summary = "Create loan statement",
            description = "Creates ClientEntity and returns a list of loan offers based on " +
                    "the provided data from MS-calculator")
    public ResponseEntity<List<LoanOfferDto>> createLoanStatement(
            @Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        log.info("Create loan from data for email: {}", loanStatementRequestDto.getEmail());
        List<LoanOfferDto> loanOfferDtoList = createLoanStatementService.getLoanOffers(loanStatementRequestDto);
        log.debug("Loan offers: {}", loanOfferDtoList);
        return ResponseEntity.ok(loanOfferDtoList);
    }

    @PostMapping("/offer/select")
    @Operation(summary = "Select loan offer to statement",
            description = "Selects a loan offer from the list of offers and saves it to the statement")
    public ResponseEntity<Void> selectLoanOffer(
            @Valid @RequestBody LoanOfferDto loanOfferDto
    ) {
        log.info("Selecting loan offer with statementId: {}", loanOfferDto.getStatementId());
        log.debug("Selecting loan offer with data: {}", loanOfferDto);
        selectLoanOfferService.selectLoanOffer(loanOfferDto);
        log.info("Loan offer selected successfully for statementId: {}", loanOfferDto.getStatementId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Calculate final loan parameters",
            description = "Calculates final loan parameters based on the selected offer and " +
                    "additional registration data")
    public ResponseEntity<Void> calculateFinalLoanParameters(
            @PathVariable("statementId") String statementId,
            @Valid @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto
    ) {
        log.info("Calculating final loan parameters for statementId: {}", statementId);
        log.debug("FinishRegistrationRequestDto: {}", finishRegistrationRequestDto);
        calculateFinalParametersService.calculateFinalParameters(statementId, finishRegistrationRequestDto);
        log.info("Final loan parameters calculated successfully for statementId: {}", statementId);
        return ResponseEntity.ok().build();
    }

}
