package ru.creditservices.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Элемент графика ежемесячных платежей")
public class PaymentScheduleElementDto {

    @Schema(description = "Номер платежа (месяц)", example = "1")
    private Integer number;

    @Schema(description = "Дата платежа", example = "2025-07-04")
    private LocalDate date;

    @Schema(description = "Общая сумма платежа (аннуитетный платёж)", example = "10450.75")
    private BigDecimal totalPayment;

    @Schema(description = "Сумма, идущая на уплату процентов", example = "1250.75")
    private BigDecimal interestPayment;

    @Schema(description = "Сумма, идущая на погашение основного долга", example = "9200.00")
    private BigDecimal debtPayment;

    @Schema(description = "Остаток основного долга после платежа", example = "198000.00")
    private BigDecimal remainingDebt;
}