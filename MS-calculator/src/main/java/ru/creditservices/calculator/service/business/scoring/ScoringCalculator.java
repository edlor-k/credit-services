package ru.creditservices.calculator.service.business.scoring;

import org.springframework.stereotype.Component;
import ru.creditservices.calculator.config.LoanProperties;
import ru.creditservices.calculator.dto.ScoringDataDto;
import ru.creditservices.calculator.model.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Component
public class ScoringCalculator {

    private final LoanProperties loanProperties;

    public ScoringCalculator(LoanProperties loanProperties) {
        this.loanProperties = loanProperties;
    }

    public BigDecimal getInsuranceCost() {
        return loanProperties.getInsuranceCost();
    }

    public BigDecimal calculateFinalRate(ScoringDataDto data) {
        BigDecimal rate = loanProperties.getBaseRate();

        switch (data.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED -> rate = rate.add(loanProperties.getSelfEmployedIncrease());
            case BUSINESS_OWNER -> rate = rate.add(loanProperties.getBusinessOwnerIncrease());
        }

        if (data.getEmployment().getPosition() != null) {
            switch (data.getEmployment().getPosition()) {
                case MIDDLE -> rate = rate.subtract(loanProperties.getPositionMiddleDiscount());
                case SENIOR -> rate = rate.subtract(loanProperties.getPositionTopDiscount());
            }
        }

        switch (data.getMaritalStatus()) {
            case MARRIED -> rate = rate.subtract(loanProperties.getMaritalMarriedDiscount());
            case DIVORCED -> rate = rate.add(loanProperties.getMaritalDivorcedIncrease());
        }

        int age = Period.between(data.getBirthdate(), LocalDate.now()).getYears();
        if (data.getGender() == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(loanProperties.getGenderMaleDiscount());
        } else if (data.getGender() == Gender.FEMALE && age >= 32 && age <= 60) {
            rate = rate.subtract(loanProperties.getGenderFemaleDiscount());
        } else if (data.getGender() == Gender.OTHER) {
            rate = rate.add(loanProperties.getGenderOtherIncrease());
        }

        if (Boolean.TRUE.equals(data.getIsInsuranceEnabled())) {
            rate = rate.subtract(loanProperties.getInsuranceRate());
        }

        if (Boolean.TRUE.equals(data.getIsSalaryClient())) {
            rate = rate.subtract(loanProperties.getSalaryDiscount());
        }

        return rate;
    }
}
