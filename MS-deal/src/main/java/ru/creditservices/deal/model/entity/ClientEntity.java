package ru.creditservices.deal.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.creditservices.deal.model.enums.Gender;
import ru.creditservices.deal.model.enums.MaritalStatus;
import ru.creditservices.deal.model.jsonb.PassportId;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "client")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthdate;

    @Column(name = "email")
    private String email;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private int dependentAmount;

    @Column(name = "passport_id")
    @JdbcTypeCode(SqlTypes.JSON)
    private PassportId passportId;

    @Column(name = "employment_id")
    @JdbcTypeCode(SqlTypes.JSON)
    private EmploymentEntity employmentId;

    }
