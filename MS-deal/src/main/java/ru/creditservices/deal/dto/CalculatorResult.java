package ru.creditservices.deal.dto;

import ru.creditservices.deal.model.enums.CalculatorResultType;

import java.util.List;

public record CalculatorResult(
        CalculatorResultType type,
        CreditDto creditDto,
        String businessDeclineReason,
        String requestErrorMessage,
        List<Violation> violations
) {

    public static CalculatorResult approved(CreditDto creditDto) {
        return new CalculatorResult(
                CalculatorResultType.APPROVED, creditDto,
                null, null, null);
    }

    public static CalculatorResult businessDecline(String reason, List<Violation> violations) {
        return new CalculatorResult(CalculatorResultType.BUSINESS_DECLINE,
                null, reason, null, violations);
    }

    public static CalculatorResult requestError(String message, List<Violation> violations) {
        return new CalculatorResult(CalculatorResultType.REQUEST_ERROR,
                null, null, message, violations);
    }
}
