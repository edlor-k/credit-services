package ru.creditservices.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Статус занятости", example = "EMPLOYED")
    @NotNull(message = "Статус занятости не может быть пустым")
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН работодателя", example = "1234567890")
    @NotNull(message = "ИНН работодателя не может быть пустым")
    private String employmentINN;

    @Schema(description = "Зарплата", example = "120000.00")
    @NotNull(message = "Зарплата должна быть указана")
    private BigDecimal salary;

    @Schema(description = "Позиция в компании", example = "JUNIOR")
    @NotNull(message = "Должность не может быть пустой")
    private Position position;

    @Schema(description = "Общий стаж работы (в месяцах)", example = "24")
    @NotNull(message = "Опыт работы должен быть указан")
    @Min(value = 18, message = "Опыт работы не может быть меньше 18 месяцев")
    private Integer workExperienceTotal;

    @Schema(description = "Опыт работы на текущем месте (в месяцах)", example = "6")
    @NotNull(message = "Опыт работы на текущем месте должен быть указан")
    @Min(value = 3, message = "Опыт работы на текущем месте не может быть меньше 3 месяцев")
    private Integer workExperienceCurrent;
}
