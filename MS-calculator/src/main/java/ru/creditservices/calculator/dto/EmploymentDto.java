package ru.creditservices.calculator.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Position;
import ru.creditservices.calculator.valid.ValidWorkExperience;

import java.math.BigDecimal;

@Data
@Builder
@ValidWorkExperience
public class EmploymentDto {

    @NotNull(message = "Статус занятости не может быть пустым")
    private EmploymentStatus employmentStatus;

    @NotNull(message = "ИНН работодателя не может быть пустым")
    private String employmentINN;

    @NotNull(message = "Зарплата должна быть указана")
    private BigDecimal salary;

    @NotNull(message = "Должность не может быть пустой")
    private Position position;

    @NotNull(message = "Опыт работы должен быть указан")
    @Min(value = 18, message = "Опыт работы не может быть меньше 18 месяцев")
    private Integer workExperienceTotal;

    @NotNull(message = "Опыт работы на текущем месте должен быть указан")
    @Min(value = 3, message = "Опыт работы на текущем месте не может быть меньше 3 месяцев")
    private Integer workExperienceCurrent;
}
