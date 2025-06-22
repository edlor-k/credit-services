package ru.creditservices.calculator.dto;

import static ru.creditservices.calculator.util.ErrorMessagesUtil.*;
import static ru.creditservices.calculator.util.RegexPatternsUtil.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Анкета клиента для генерации кредитных предложений")
public class LoanStatementRequestDto {

    @Schema(description = "Запрашиваемая сумма кредита", example = "500000")
    @NotNull(message = EMPTY_AMOUNT)
    @DecimalMin(value = "1", message = NEGATIVE_AMOUNT)
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "24")
    @NotNull(message = EMPTY_TERM)
    @Min(value = 1, message = NEGATIVE_TERM)
    private Integer term;

    @Schema(description = "Имя клиента", example = "Petr")
    @NotBlank(message = EMPTY_NAME)
    @Size(min = 2, max = 30, message = INVALID_NAME)
    private String firstName;

    @Schema(description = "Фамилия клиента", example = "Ivanov")
    @NotBlank(message = EMPTY_LASTNAME)
    @Size(min = 2, max = 30, message = INVALID_LASTNAME)
    private String lastName;

    @Schema(description = "Отчество клиента", example = "Ivanovich")
    @Size(min = 2, max = 30, message = INVALID_MIDDLENAME)
    private String middleName;

    @Schema(description = "Email клиента", example = "ivanov@example.com")
    @NotBlank(message = EMPTY_EMAIL)
    @Pattern(regexp = EMAIL, message = INVALID_EMAIL)
    private String email;

    @Schema(description = "Дата рождения клиента", example = "1985-05-20")
    @NotNull(message = EMPTY_BIRTHDATE)
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта", example = "1234")
    @NotBlank(message = EMPTY_PASSPORT_SERIES)
    @Pattern(regexp = PASSPORT_SERIES, message = INVALID_PASSPORT_SERIES)
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "567890")
    @NotBlank(message = EMPTY_PASSPORT_NUMBER)
    @Pattern(regexp = PASSPORT_NUMBER, message = INVALID_PASSPORT_NUMBER)
    private String passportNumber;
}
