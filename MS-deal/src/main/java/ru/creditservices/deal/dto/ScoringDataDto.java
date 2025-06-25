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

import java.math.BigDecimal;
import java.time.LocalDate;

import static ru.creditservices.deal.util.ErrorMessagesUtil.*;
import static ru.creditservices.deal.util.RegexPatternsUtil.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoringDataDto {

    @Schema(description = "Запрашиваемая сумма кредита", example = "500000.00")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @DecimalMin(value = "1", message = NEGATIVE_AMOUNT)
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Min(value = 1, message = NEGATIVE_TERM)
    private Integer term;

    @Schema(description = "Имя клиента", example = "Иван")
    @NotBlank(message = REQUIRED_PARAM_EMPTY)
    @Size(min = 2, max = 30, message = INVALID_NAME)
    private String firstName;

    @Schema(description = "Фамилия клиента", example = "Иванов")
    @NotBlank(message = REQUIRED_PARAM_EMPTY)
    @Size(min = 2, max = 30, message = INVALID_LASTNAME)
    private String lastName;

    @Schema(description = "Отчество клиента", example = "Иванович")
    @Size(min = 2, max = 30, message = INVALID_MIDDLENAME)
    private String middleName;

    @Schema(description = "Пол клиента", example = "MALE")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private Gender gender;

    @Schema(description = "Дата рождения клиента", example = "1990-01-01")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта клиента", example = "1234")
    @NotBlank(message = REQUIRED_PARAM_EMPTY)
    @Pattern(regexp = PASSPORT_SERIES, message = INVALID_PASSPORT_SERIES)
    private String passportSeries;

    @Schema(description = "Номер паспорта клиента", example = "567890")
    @NotBlank(message = REQUIRED_PARAM_EMPTY)
    @Pattern(regexp = PASSPORT_NUMBER, message = INVALID_PASSPORT_NUMBER)
    private String passportNumber;

    @Schema(description = "Дата выдачи паспорта клиента", example = "2010-01-01")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @PastOrPresent(message = FUTURE_PASSPORT_ISSUE_DATE)
    private LocalDate passportIssueDate;

    @Schema(description = "Кем выдан паспорт клиента", example = "УФМС России")
    @NotBlank(message = REQUIRED_PARAM_EMPTY)
    @Size(min = 3, max = 100, message = INVALID_PASSPORT_ISSUE_BRANCH)
    private String passportIssueBranch;

    @Schema(description = "Семейное положение клиента", example = "MARRIED")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев клиента", example = "0")
    @NotNull(message = NEGATIVE_DEPENDENT_AMOUNT)
    @Min(value = 0, message = NEGATIVE_DEPENDENT_AMOUNT)
    private Integer dependentAmount;

    @Schema
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Valid
    private EmploymentDto employment;

    @Schema(description = "Номер банковского счета клиента", example = "12345678901234567890")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    @Pattern(regexp = ACCOUNT_NUMBER, message = INVALID_ACCOUNT_NUMBER)
    private String accountNumber;

    @Schema(description = "Наличие опции страховки", example = "true")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private Boolean isInsuranceEnabled;

    @Schema(description = "Статус зарплатного клиента", example = "true")
    @NotNull(message = REQUIRED_PARAM_EMPTY)
    private Boolean isSalaryClient;
}
