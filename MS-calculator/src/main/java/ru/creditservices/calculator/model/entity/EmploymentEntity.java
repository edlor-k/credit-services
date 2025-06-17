package ru.creditservices.calculator.model.entity;

import lombok.Builder;
import lombok.Data;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Position;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentEntity {
        private EmploymentStatus employmentStatus;
        private String employmentINN;
        private BigDecimal salary;
        private Position position;
        private Integer workExperienceTotal;
        private Integer workExperienceCurrent;
}

