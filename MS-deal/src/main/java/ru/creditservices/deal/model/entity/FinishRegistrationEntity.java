package ru.creditservices.deal.model.entity;

import lombok.Builder;
import lombok.Data;
import ru.creditservices.deal.model.enums.Gender;
import ru.creditservices.deal.model.enums.MaritalStatus;

import java.time.LocalDate;

@Data
@Builder
public class FinishRegistrationEntity {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssuedDate;
    private String passportIssueBranch;
    private EmploymentEntity employment;
    private String accountNumber;
}
