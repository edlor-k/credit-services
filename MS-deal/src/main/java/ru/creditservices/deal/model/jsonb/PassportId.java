package ru.creditservices.deal.model.jsonb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportId {
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issuedDate;
}
