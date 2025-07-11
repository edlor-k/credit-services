package ru.creditservices.deal.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "passport", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"series", "number"})
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "passport_uuid")
    private UUID passportUuid;

    @Column(name = "series", nullable = false, length = 4)
    @NotNull
    private String series;

    @Column(name = "number", nullable = false, length = 6)
    @NotNull
    private String number;

    @Column(name = "issue_branch", length = 100)
    @Size(min = 1, max = 100)
    private String issueBranch;

    @Column(name = "issue_date")
    private LocalDate issueDate;
}
