package ru.creditservices.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.creditservices.deal.model.enums.Gender;
import ru.creditservices.deal.model.enums.MaritalStatus;


import java.time.LocalDate;

import static ru.creditservices.deal.util.ErrorMessagesUtil.*;
import static ru.creditservices.deal.util.RegexPatternsUtil.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос с финальной информацией о клиенте")
public class FinishRegistrationRequestDto {

    @Schema(description = "Пол клиента", example = "MALE")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private Gender gender;

    @Schema(description = "Семейное положение клиента", example = "MARRIED")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев клиента", example = "0")
    @NotNull(message = NEGATIVE_DEPENDENT_AMOUNT)
    @Min(value = 0, message = NEGATIVE_DEPENDENT_AMOUNT)
    private Integer dependentAmount;

    @Schema(description = "Дата выдачи паспорта клиента", example = "2010-01-01")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private LocalDate passportIssuedDate;

    @Schema(description = "Кем выдан паспорт клиента", example = "УФМС России")
    @NotBlank(message = REQUIRED_PARAM_EMPTY)
    @Size(min = 3, max = 100, message = INVALID_PASSPORT_ISSUE_BRANCH)
    private String passportIssueBranch;

    @Schema
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Valid
    private EmploymentDto employment;

    @Schema(description = "Номер банковского счета клиента", example = "12345678901234567890")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Pattern(regexp = ACCOUNT_NUMBER, message = INVALID_ACCOUNT_NUMBER)
    private String accountNumber;
}
