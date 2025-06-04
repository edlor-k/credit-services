package ru.creditservices.calculator.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.creditservices.calculator.valid.Adult;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanStatementRequestDto {
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

    // email клиента
    @NotBlank(message = "Email клиента должен быть заполнен")
    @Pattern(
            regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$",
            message = "Некорректный формат email"
    )
    private String email;

    // дата рождения клиента'
    @NotNull(message = "Дата рождения клиента должна быть заполнена")
    @Adult(message = "Клиент должен быть совершеннолетним")
    private LocalDate birthdate;

    // серия и номер паспорта клиента
    @NotBlank(message = "Серия паспорта должна быть заполнена")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;

    @NotBlank(message = "Номер паспорта должен быть заполнен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;
}
