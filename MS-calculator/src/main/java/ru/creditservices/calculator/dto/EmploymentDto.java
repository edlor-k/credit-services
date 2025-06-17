package ru.creditservices.calculator.dto;

import static ru.creditservices.calculator.util.ErrorMessagesUtil.*;
import static ru.creditservices.calculator.util.RegexPatternsUtil.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Position;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto {

    @Schema(description = "Статус занятости", example = "EMPLOYED")
    @NotNull(message = EMPTY_EMPLOYMENT_STATUS)
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН работодателя", example = "1234567890")
    @NotNull(message = EMPTY_EMPLOYERS_INN)
    @Pattern(regexp = INN, message = INVALID_EMPLOYERS_INN)
    private String employmentINN;

    @Schema(description = "Зарплата", example = "120000.00")
    @NotNull(message = EMPTY_SALARY)
    @Min(value = 0, message = NEGATIVE_SALARY)
    private BigDecimal salary;

    @Schema(description = "Позиция в компании", example = "JUNIOR")
    private Position position;

    @Schema(description = "Общий стаж работы (в месяцах)", example = "24")
    @NotNull(message = INVALID_WORK_EXP_TOTAL)
    @Min(value = 0, message = NEGATIVE_WORK_EXP_TOTAL)
    private Integer workExperienceTotal;

    @Schema(description = "Опыт работы на текущем месте (в месяцах)", example = "6")
    @NotNull(message = INVALID_WORK_EXP_CURRENT)
    @Min(value = 0, message = NEGATIVE_WORK_EXP_CURRENT)
    private Integer workExperienceCurrent;
}
