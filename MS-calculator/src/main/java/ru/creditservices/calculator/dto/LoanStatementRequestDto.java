package ru.creditservices.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.creditservices.calculator.valid.Adult;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Анкета клиента для генерации кредитных предложений")
public class LoanStatementRequestDto {

    @Schema(description = "Запрашиваемая сумма кредита", example = "500000")
    @NotNull(message = "Сумма кредита не может быть пустой")
    @DecimalMin(value = "20000", message = "Сумма кредита должна быть не менее 20 000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "24")
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

    @Schema(description = "Email клиента", example = "ivanov@example.com")
    @NotBlank(message = "Email клиента должен быть заполнен")
    @Pattern(
            regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$",
            message = "Некорректный формат email"
    )
    private String email;

    @Schema(description = "Дата рождения клиента", example = "1985-05-20")
    @NotNull(message = "Дата рождения клиента должна быть заполнена")
    @Adult(message = "Клиент должен быть совершеннолетним")
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта", example = "1234")
    @NotBlank(message = "Серия паспорта должна быть заполнена")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "567890")
    @NotBlank(message = "Номер паспорта должен быть заполнен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;
}
