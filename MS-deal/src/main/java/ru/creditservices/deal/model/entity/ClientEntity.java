package ru.creditservices.deal.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.creditservices.deal.model.enums.Gender;
import ru.creditservices.deal.model.enums.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "client",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = {"passport_id"}),
                @UniqueConstraint(columnNames = {"account_number"})
        })
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "last_name", nullable = false, length = 30)
    @NotNull
    @Size(min = 2, max = 30)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 30)
    @NotNull
    @Size(min = 2, max = 30)
    private String firstName;

    @Column(name = "middle_name", length = 30)
    @Size(min = 2, max = 30)
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    @NotNull
    private LocalDate birthdate;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    @NotNull
    @Size(max = 100)
    private String email;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    @Min(0)
    @Max(20)
    private int dependentAmount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "passport_id", referencedColumnName = "passport_uuid", unique = true, nullable = false)
    @NotNull
    private PassportEntity passportId;

    @Column(name = "employment_id")
    @JdbcTypeCode(SqlTypes.JSON)
    private EmploymentEntity employmentId;

    @Column(name = "account_number", length = 30, unique = true)
    @Size(min = 5, max = 30)
    private String accountNumber;
}
