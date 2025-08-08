package ru.creditservices.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportDto {
    private UUID passportId;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
