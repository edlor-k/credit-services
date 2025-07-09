package ru.creditservices.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.creditservices.deal.model.entity.*;
import ru.creditservices.deal.model.enums.Gender;
import ru.creditservices.deal.model.enums.MaritalStatus;
import ru.creditservices.deal.service.impl.ScoringDataAssembler;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ScoringDataAssemblerTest {

    @Test
    @DisplayName("Корректная работа ассемблера ScoringDataEntity из StatementEntity и FinishRegistrationEntity")
    void assembleScoringDataEntityReturnsCorrectScoringData() {
        ClientEntity client = ClientEntity.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportId(PassportEntity.builder().series("1234").number("567890").build())
                .build();

        LoanOfferEntity offer = LoanOfferEntity.builder()
                .requestedAmount(new BigDecimal("500000"))
                .term(24)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        StatementEntity statement = StatementEntity.builder()
                .client(client)
                .appliedOffer(offer)
                .build();

        FinishRegistrationEntity finishRegistration = FinishRegistrationEntity.builder()
                .gender(Gender.MALE)
                .passportIssuedDate(LocalDate.of(2010, 5, 15))
                .passportIssueBranch("770-001")
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(2)
                .employment(EmploymentEntity.builder().build())
                .accountNumber("40817810099910004312")
                .build();

        ScoringDataAssembler assembler = new ScoringDataAssembler();

        ScoringDataEntity scoringData = assembler.assembleScoringDataEntity(statement, finishRegistration);

        assertEquals(offer.getRequestedAmount(), scoringData.getAmount());
        assertEquals(offer.getTerm(), scoringData.getTerm());
        assertEquals(client.getFirstName(), scoringData.getFirstName());
        assertEquals(client.getLastName(), scoringData.getLastName());
        assertEquals(client.getMiddleName(), scoringData.getMiddleName());
        assertEquals(client.getBirthdate(), scoringData.getBirthdate());
        assertEquals(client.getPassportId().getSeries(), scoringData.getPassportSeries());
        assertEquals(client.getPassportId().getNumber(), scoringData.getPassportNumber());
        assertEquals(Gender.MALE, scoringData.getGender());
        assertEquals(LocalDate.of(2010, 5, 15), scoringData.getPassportIssueDate());
        assertEquals("770-001", scoringData.getPassportIssueBranch());
        assertEquals(MaritalStatus.SINGLE, scoringData.getMaritalStatus());
        assertEquals(2, scoringData.getDependentAmount());
        assertEquals(finishRegistration.getEmployment(), scoringData.getEmployment());
        assertEquals(offer.getIsInsuranceEnabled(), scoringData.getIsInsuranceEnabled());
        assertEquals(offer.getIsSalaryClient(), scoringData.getIsSalaryClient());
        assertEquals(finishRegistration.getAccountNumber(), scoringData.getAccountNumber());
    }
}