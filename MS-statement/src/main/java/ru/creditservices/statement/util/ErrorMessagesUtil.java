package ru.creditservices.statement.util;

public class ErrorMessagesUtil {
    private ErrorMessagesUtil() {}

    public static final String REQUIRED_PARAM_EMPTY = "Отсутствует обязательный параметр: ";

    public static final String INVALID_EMAIL = "Некорректный формат email";
    public static final String INVALID_PASSPORT_SERIES = "Серия паспорта должна содержать 4 цифры";
    public static final String INVALID_PASSPORT_NUMBER = "Номер паспорта должен содержать 6 цифр";
    public static final String INVALID_NAME = "Имя клиента должно быть от 2 до 30 символов";
    public static final String INVALID_LASTNAME = "Фамилия клиента должна быть от 2 до 30 символов";
    public static final String INVALID_MIDDLENAME = "Отчество клиента должно быть от 2 до 30 символов";
    public static final String NEGATIVE_AMOUNT = "Сумма кредита должна быть положительной";
    public static final String NEGATIVE_TERM = "Срок кредита должен быть положительным";

    public static final String PRESCORING_FIRSTNAME_INVALID = "Имя должно содержать от 2 до 30 латинских букв";
    public static final String PRESCORING_LASTNAME_INVALID = "Фамилия должна содержать от 2 до 30 латинских букв";
    public static final String PRESCORING_MIDDLENAME_INVALID = "Отчество должно содержать от 2 до 30 латинских букв";
    public static final String PRESCORING_AMOUNT_INVALID = "Сумма кредита должна быть не менее 20 000";
    public static final String PRESCORING_TERM_INVALID = "Срок кредита должен быть не менее 6 месяцев";
    public static final String PRESCORING_BIRTHDATE_REQUIRED = "Дата рождения должна быть указана";
    public static final String PRESCORING_AGE_INVALID = "Заемщик должен быть старше 18 лет";
    public static final String PRESCORING_EMAIL_INVALID = "Некорректный формат email";
    public static final String PRESCORING_PASSPORT_SERIES_INVALID = "Серия паспорта должна содержать 4 цифры";
    public static final String PRESCORING_PASSPORT_NUMBER_INVALID = "Номер паспорта должен содержать 6 цифр";
}
