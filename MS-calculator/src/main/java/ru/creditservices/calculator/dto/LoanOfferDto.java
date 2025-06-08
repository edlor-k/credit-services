package ru.creditservices.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Предложение по кредиту")
@Data
@Builder
public class LoanOfferDto {

    @Schema(description = "ID заявки", example = "d3b07384-d9b2-4c1e-9df0-2f6e221c8a5b")
    private UUID statementId;

    @Schema(description = "Запрошенная сумма", example = "500000")
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма с учетом условий", example = "550000")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредита", example = "24")
    private Integer term;

    @Schema(description = "Ежемесячный платёж", example = "22916.67")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "14.9")
    private BigDecimal rate;

    @Schema(description = "Страховка включена", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Клиент является зарплатным", example = "false")
    private Boolean isSalaryClient;
}