package ru.creditservices.calculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "loan")
@Data
public class LoanProperties {
    private BigDecimal baseRate;
    private BigDecimal insuranceRate;
    private BigDecimal insuranceCost;
    private BigDecimal salaryDiscount;

    private BigDecimal selfEmployedIncrease;
    private BigDecimal businessOwnerIncrease;

    private BigDecimal positionMiddleDiscount;
    private BigDecimal positionTopDiscount;

    private BigDecimal maritalMarriedDiscount;
    private BigDecimal maritalDivorcedIncrease;

    private BigDecimal genderFemaleDiscount;
    private BigDecimal genderMaleDiscount;
    private BigDecimal genderOtherIncrease;
}
