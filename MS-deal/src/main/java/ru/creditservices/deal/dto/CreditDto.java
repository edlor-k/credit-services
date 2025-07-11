package ru.creditservices.deal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Schema(description = "Информация о кредите")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditDto {

    @Schema(description = "Сумма кредита", example = "1000000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита (в месяцах)", example = "36")
    private Integer term;

    @Schema(description = "Ежемесячный платёж", example = "32500.50")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "12.5")
    private BigDecimal rate;

    @Schema(description = "Полная стоимость кредита", example = "1170000")
    private BigDecimal psk;

    @Schema(description = "Страховка включена", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Клиент является зарплатным", example = "false")
    private Boolean isSalaryClient;

    @Schema(description = "График платежей")
    private List<PaymentScheduleElementDto> paymentSchedule;
}