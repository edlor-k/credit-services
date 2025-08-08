package ru.creditservices.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.creditservices.gateway.model.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementDto {
    private UUID statementId;
    private ClientDto client;
    private CreditDto credit;
    private ApplicationStatus status;
    private LocalDateTime creationDate;
    private LoanOfferDto appliedOffer;
    private LocalDateTime signDate;
    private String sesCode;
    private List<StatusHistoryElementDto> statusHistory;
}
