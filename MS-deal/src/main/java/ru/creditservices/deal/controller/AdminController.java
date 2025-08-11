package ru.creditservices.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditservices.deal.dto.StatementDto;
import ru.creditservices.deal.service.DealService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deal Admin API", description = "Administrative operations for loan deals")
public class AdminController {

    private final DealService dealService;

    @GetMapping("/statement")
    @Operation(summary = "Get all statements for admin",
            description = "Returns a list of all statements for administrative purposes")
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        log.info("Fetching all statements for admin");
        List<StatementDto> statements = dealService.getAllStatements();
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/statement/{statementId}")
    @Operation(summary = "Get statement by ID for admin",
            description = "Returns a statement details for administrative purposes")
    public ResponseEntity<StatementDto> getStatementById(@PathVariable UUID statementId) {
        log.info("Fetching statement with id {} for admin", statementId);
        return ResponseEntity.ok(dealService.getStatementById(statementId));
    }

    @PutMapping("/statement/status")
    @Operation(summary = "Update statement status",
            description = "Updates the status of a statement by its ID")
    public ResponseEntity<Void> updateStatementStatus(@RequestParam UUID statementId,
                                                      @RequestParam String status) {
        log.info("Updating statement status for statementId: {} to status: {}", statementId, status);
        dealService.updateStatementStatus(statementId, status);
        return ResponseEntity.ok().build();
    }
}
