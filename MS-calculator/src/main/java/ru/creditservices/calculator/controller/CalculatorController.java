package ru.creditservices.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "Получение предложений по кредиту",
            description = "Возращает лист из 4 предложений по кредиту или отказ " +
                    "на основе данных клиента и параметров кредита.")
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для прескоринга",
            required = true
            )
            @RequestBody LoanStatementRequestDto request) {
        log.info("Received loan statement request: {}", request);
        return ResponseEntity.ok(calculatorService.prescoring(request));
    }

    @PostMapping("/calc")
    @Operation(summary = "Расчет кредита, основанный на данных клиента",
            description = "Возвращает информацию о кредите, " +
                    "включая сумму, срок, ежемесячный платёж и другие параметры.")
    public ResponseEntity<CreditDto> calculateCredit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные клиента для расчёта кредита",
                    required = true
            )
            @RequestBody ScoringDataDto request) {
        log.info("Received scoring data: {}", request);
        return ResponseEntity.ok(calculatorService.calculate(request));
    }
}