package ru.creditservices.calculator.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreditDto {

    // сумма кредита
    private BigDecimal amount;

    // срок кредита (в месяцах)
    private Integer term;

    // ежемесячный платеж
    private BigDecimal monthlyPayment;

    // ставка по кредиту
    private BigDecimal rate;

    // полная стоимость кредита
    private BigDecimal psk;

    // наличие опции страховки
    private Boolean isInsuranceEnabled;

    // статус зарплатного клиента
    private Boolean isSalaryClient;

    // график платежей
    private List<PaymentScheduleElementDto> paymentSchedule;
}
