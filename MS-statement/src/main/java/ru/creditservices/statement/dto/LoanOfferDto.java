package ru.creditservices.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static ru.creditservices.statement.util.ErrorMessagesUtil.*;

@Schema(description = "Предложение по кредиту")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferDto {

    @Schema(description = "ID заявки", example = "d3b07384-d9b2-4c1e-9df0-2f6e221c8a5b")
    private UUID statementId;

    @Schema(description = "Запрашиваемая сумма кредита", example = "500000")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @DecimalMin(value = "1", message = NEGATIVE_AMOUNT)
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма с учетом условий", example = "550000")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @DecimalMin(value = "1", message = NEGATIVE_AMOUNT)
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредита", example = "24")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Min(value = 1, message = NEGATIVE_TERM)
    private Integer term;

    @Schema(description = "Ежемесячный платёж", example = "22916.67")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @DecimalMin(value = "1", message = NEGATIVE_AMOUNT)
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "14.9")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @DecimalMin(value = "0.1", message = NEGATIVE_TERM)
    private BigDecimal rate;

    @Schema(description = "Наличие опции страховки", example = "true")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private Boolean isInsuranceEnabled;

    @Schema(description = "Статус зарплатного клиента", example = "true")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private Boolean isSalaryClient;
}