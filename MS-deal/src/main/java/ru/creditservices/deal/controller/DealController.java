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
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.service.DealService;
import ru.creditservices.deal.service.DocumentsService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deal Microservice", description = "API for managing loan deals")
public class DealController {

    private final DealService dealService;
    private final DocumentsService documentsService;

    @PostMapping("/statement")
    @Operation(summary = "Create loan statement",
            description = "Creates ClientEntity and returns a " +
                    "list of loan offers based on the provided data from MS-calculator")
    public ResponseEntity<List<LoanOfferDto>> createLoanStatement(@Valid @RequestBody LoanStatementRequestDto dto) {
        log.info("Create loan from data for email: {}", dto.getEmail());
        return ResponseEntity.ok(dealService.createLoanStatement(dto));
    }

    @PostMapping("/offer/select")
    @Operation(summary = "Select loan offer to statement",
            description = "Selects a loan offer from the list of offers and saves it to the statement")
    public ResponseEntity<Void> selectLoanOffer(@Valid @RequestBody LoanOfferDto dto) {
        log.info("Selecting loan offer with statementId: {}", dto.getStatementId());
        dealService.selectLoanOffer(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Calculate final loan parameters",
            description = "Calculates final loan parameters based on the selected " +
                    "offer and additional registration data")
    public ResponseEntity<Void> calculateFinalLoanParameters(@PathVariable("statementId") UUID statementId,
                                                             @Valid @RequestBody FinishRegistrationRequestDto dto) {
        log.info("Calculating final loan parameters for statementId: {}", statementId);
        dealService.calculateFinalLoanParameters(statementId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/send")
    @Operation(summary = "Send documents to client",
            description = "Triggers an email with document sending instructions")
    public ResponseEntity<Void> requestToSendDocuments(@PathVariable UUID statementId) {
        log.info("Requesting to send documents for statementId: {}", statementId);
        dealService.sendDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/sign")
    @Operation(summary = "Ask client to sign documents", description = "Triggers an email with signing instructions")
    public ResponseEntity<Void> requestToSignDocuments(@PathVariable UUID statementId) {
        log.info("Requesting to sign documents for statementId: {}", statementId);
        dealService.requestToSignDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/{code}")
    @Operation(summary = "Confirm document signing", description = "Sends confirmation email after signing")
    public ResponseEntity<Void> confirmDocumentSigning(@PathVariable UUID statementId, @PathVariable String code) {
        log.info("Confirming document signing for statementId: {}", statementId);
        dealService.confirmDocumentSigning(statementId, code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/statement")
    @Operation(summary = "Get all statements for admin",
            description = "Returns a list of all statements for administrative purposes")
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        log.info("Fetching all statements for admin");
        List<StatementDto> statements = dealService.getAllStatements();
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/admin/statement/{statementId}")
    @Operation(summary = "Get all statements for admin",
            description = "Returns a list of all statements for administrative purposes")
    public ResponseEntity<StatementDto> getStatementById(@PathVariable UUID statementId) {
        log.info("Fetching statement with id {} for admin", statementId);
        return ResponseEntity.ok(dealService.getStatementById(statementId));
    }

    @PutMapping
    @Operation(summary = "Update statement status",
            description = "Updates the status of a statement by its ID")
    public ResponseEntity<Void> updateStatementStatus(@RequestParam UUID statementId,
                                                                   @RequestParam String status) {
        log.info("Updating statement status for statementId: {} to status: {}", statementId, status);
        documentsService.updateStatementStatus(statementId, status);
        return ResponseEntity.ok().build();
    }
}
