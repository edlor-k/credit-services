package ru.creditservices.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.creditservices.calculator.model.enums.Gender;
import ru.creditservices.calculator.model.enums.MaritalStatus;
import ru.creditservices.calculator.valid.ValidAge;
import ru.creditservices.calculator.valid.ValidAmountAndSalary;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@ValidAmountAndSalary
public class ScoringDataDto {
    @Schema(description = "Запрашиваемая сумма кредита", example = "500000.00")
    @NotNull(message = "Сумма кредита не может быть пустой")
    @DecimalMin(value = "20000", message = "Сумма кредита должна быть не менее 20 000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    @NotNull(message = "Срок кредита не может быть пустым")
    @Min(value = 6, message = "Срок кредита должен быть не менее 6 месяцев")
    private Integer term;

    @Schema(description = "Имя клиента", example = "Иван")
    @NotBlank(message = "Имя клиента должно быть заполнено")
    @Size(min = 2, max = 30, message = "Имя клиента должно быть от 2 до 30 символов")
    private String firstName;

    @Schema(description = "Фамилия клиента", example = "Иванов")
    @NotBlank(message = "Фамилия клиента должна быть заполнена")
    @Size(min = 2, max = 30, message = "Фамилия клиента должна быть от 2 до 30 символов")
    private String lastName;

    @Schema(description = "Отчество клиента", example = "Иванович")
    @Size(min = 2, max = 30, message = "Отчество клиента должно быть от 2 до 30 символов")
    private String middleName;

    @Schema(description = "Пол клиента", example = "MALE")
    @NotNull(message = "Пол клиента должен быть указан")
    private Gender gender;

    @Schema(description = "Дата рождения клиента", example = "1990-01-01")
    @NotNull(message = "Дата рождения клиента должна быть заполнена")
    @ValidAge
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта клиента", example = "1234")
    @NotBlank(message = "Серия паспорта должна быть заполнена")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;

    @Schema(description = "Номер паспорта клиента", example = "567890")
    @NotBlank(message = "Номер паспорта должен быть заполнен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;

    @Schema(description = "Дата выдачи паспорта клиента", example = "2010-01-01")
    @NotNull(message = "Дата выдачи паспорта должна быть заполнена")
    @PastOrPresent(message = "Дата выдачи паспорта не может быть в будущем")
    private LocalDate passportIssueDate;

    @Schema(description = "Кем выдан паспорт клиента", example = "УФМС России")
    @NotBlank(message = "Кем выдан паспорт должен быть заполнен")
    @Size(min = 3, max = 100, message = "Поле 'кем выдан' должно содержать от 3 до 100 символов")
    private String passportIssueBranch;

    @Schema(description = "Семейное положение клиента", example = "MARRIED")
    @NotNull(message = "Семейное положение клиента должно быть указано")
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев клиента", example = "0")
    @NotNull
    @Min(value = 0, message = "Количество иждивенцев не может быть отрицательным")
    private Integer dependentAmount;

    @Schema
    @NotNull(message = "Статус занятости клиента должен быть указан")
    @Valid
    private EmploymentDto employment;

    @Schema(description = "Номер банковского счета клиента", example = "12345678901234567890")
    @NotNull(message = "Номер банковского счета не может быть пустым")
    @Pattern(regexp = "^\\d{20}$", message = "Номер банковского счета должен содержать 20 цифр")
    private String accountNumber;

    @Schema(description = "Наличие опции страховки", example = "true")
    @NotNull(message="Опция страховки должна быть указана")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Статус зарплатного клиента", example = "true")
    @NotNull(message = "Статус зарплатного клиента должен быть указан")
    private Boolean isSalaryClient;
}
