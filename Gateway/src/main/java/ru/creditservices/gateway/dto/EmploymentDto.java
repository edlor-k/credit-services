package ru.creditservices.gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.creditservices.gateway.model.enums.EmploymentStatus;
import ru.creditservices.gateway.model.enums.Position;

import java.math.BigDecimal;

import static ru.creditservices.gateway.util.ErrorMessagesUtil.*;
import static ru.creditservices.gateway.util.RegexPatternsUtil.INN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о занятости клиента")
public class EmploymentDto {

    @Schema(description = "Статус занятости", example = "EMPLOYED")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН работодателя", example = "1234567890")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Pattern(regexp = INN, message = INVALID_EMPLOYERS_INN)
    private String employmentINN;

    @Schema(description = "Зарплата", example = "120000.00")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Min(value = 0, message = NEGATIVE_SALARY)
    private BigDecimal salary;

    @Schema(description = "Позиция в компании", example = "WORKER")
    private Position position;

    @Schema(description = "Общий стаж работы (в месяцах)", example = "24")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Min(value = 0, message = NEGATIVE_WORK_EXP_TOTAL)
    private Integer workExperienceTotal;

    @Schema(description = "Опыт работы на текущем месте (в месяцах)", example = "6")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Min(value = 0, message = NEGATIVE_WORK_EXP_CURRENT)
    private Integer workExperienceCurrent;
}
