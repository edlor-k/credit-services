package ru.creditservices.deal.model.jsonb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.creditservices.deal.model.enums.ApplicationStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistoryElement {
    private ApplicationStatus status;
    private LocalDateTime time;
}
