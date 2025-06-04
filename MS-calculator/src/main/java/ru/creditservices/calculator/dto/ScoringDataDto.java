package ru.creditservices.calculator.dto;

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
    // запрашиваемая сумма кредита
    @NotNull(message = "Сумма кредита не может быть пустой")
    @DecimalMin(value = "20000", message = "Сумма кредита должна быть не менее 20 000")
    private BigDecimal amount;

    // срок кредита в месяцах
    @NotNull(message = "Срок кредита не может быть пустым")
    @Min(value = 6, message = "Срок кредита должен быть не менее 6 месяцев")
    private Integer term;

    // ФИО клиента
    @NotBlank(message = "Имя клиента должно быть заполнено")
    @Size(min = 2, max = 30, message = "Имя клиента должно быть от 2 до 30 символов")
    private String firstName;

    @NotBlank(message = "Фамилия клиента должна быть заполнена")
    @Size(min = 2, max = 30, message = "Фамилия клиента должна быть от 2 до 30 символов")
    private String lastName;

    @Size(min = 2, max = 30, message = "Отчество клиента должно быть от 2 до 30 символов")
    private String middleName;

    // пол клиента
    @NotNull(message = "Пол клиента должен быть указан")
    private Gender gender;

    // дата рождения клиента
    @NotNull(message = "Дата рождения клиента должна быть заполнена")
    @ValidAge
    private LocalDate birthdate;

    // паспортные данные клиента
    @NotBlank(message = "Серия паспорта должна быть заполнена")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;

    @NotBlank(message = "Номер паспорта должен быть заполнен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;

    @NotNull(message = "Дата выдачи паспорта должна быть заполнена")
    @PastOrPresent(message = "Дата выдачи паспорта не может быть в будущем")
    private LocalDate passportIssueDate;


    @NotBlank(message = "Кем выдан паспорт должен быть заполнен")
    @Size(min = 3, max = 100, message = "Поле 'кем выдан' должно содержать от 3 до 100 символов")
    private String passportIssueBranch;

    // семейное положение клиента
    @NotNull(message = "Семейное положение клиента должно быть указано")
    private MaritalStatus maritalStatus;

    @NotNull
    @Min(value = 0, message = "Количество иждивенцев не может быть отрицательным")
    private Integer dependentAmount;

    // рабочий статус клиента
    @NotNull(message = "Статус занятости клиента должен быть указан")
    @Valid
    private EmploymentDto employment;

    // информация о банковском аккаунте клиента
    @NotNull(message = "Номер банковского счета не может быть пустым")
    @Pattern(regexp = "^\\d{20}$", message = "Номер банковского счета должен содержать 20 цифр")
    private String accountNumber;

    // включена ли страховка
    @NotNull(message="Опция страховки должна быть указана")
    private Boolean isInsuranceEnabled;

    // является ли клиент зарплатным
    @NotNull(message = "Статус зарплатного клиента должен быть указан")
    private Boolean isSalaryClient;
}
