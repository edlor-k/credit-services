package ru.creditservices.deal.service.impl;

import org.springframework.stereotype.Component;
import ru.creditservices.deal.model.entity.ClientEntity;
import ru.creditservices.deal.model.entity.FinishRegistrationEntity;
import ru.creditservices.deal.model.entity.ScoringDataEntity;
import ru.creditservices.deal.model.entity.StatementEntity;

@Component
public class ScoringDataAssembler {
    public ScoringDataEntity assembleScoringDataEntity(
            StatementEntity statementEntity, FinishRegistrationEntity finishRegistrationEntity) {
        ClientEntity clientEntity = statementEntity.getClient();

        return ScoringDataEntity.builder()
                .amount(statementEntity.getAppliedOffer().getRequestedAmount())
                .term(statementEntity.getAppliedOffer().getTerm())
                .firstName(clientEntity.getFirstName())
                .lastName(clientEntity.getLastName())
                .middleName(clientEntity.getMiddleName())
                .gender(finishRegistrationEntity.getGender())
                .birthdate(clientEntity.getBirthdate())
                .passportSeries(clientEntity.getPassportId().getSeries())
                .passportNumber(clientEntity.getPassportId().getNumber())
                .passportIssueDate(finishRegistrationEntity.getPassportIssuedDate())
                .passportIssueBranch(finishRegistrationEntity.getPassportIssueBranch())
                .maritalStatus(finishRegistrationEntity.getMaritalStatus())
                .dependentAmount(finishRegistrationEntity.getDependentAmount())
                .employment(finishRegistrationEntity.getEmployment())
                .isInsuranceEnabled(statementEntity.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(statementEntity.getAppliedOffer().getIsSalaryClient())
                .accountNumber(finishRegistrationEntity.getAccountNumber())
                .build();
    }
}