package ru.creditservices.deal.util;

public class ErrorMessagesUtil {
    private ErrorMessagesUtil() {}

    public static final String REQUIRED_PARAM_EMPTY = "Отсутствует обязательный параметр: ";

    public static final String INVALID_EMAIL = "Некорректный формат email";
    public static final String INVALID_PASSPORT_SERIES = "Серия паспорта должна содержать 4 цифры";
    public static final String INVALID_PASSPORT_NUMBER = "Номер паспорта должен содержать 6 цифр";
    public static final String INVALID_PASSPORT_ISSUE_BRANCH = "Поле 'кем выдан' должно содержать от 3 до 100 символов";
    public static final String INVALID_ACCOUNT_NUMBER = "Номер банковского счета должен содержать 20 цифр";
    public static final String INVALID_NAME = "Имя клиента должно быть от 2 до 30 символов";
    public static final String INVALID_LASTNAME = "Фамилия клиента должна быть от 2 до 30 символов";
    public static final String INVALID_MIDDLENAME = "Отчество клиента должно быть от 2 до 30 символов";
    public static final String NEGATIVE_AMOUNT = "Сумма кредита должна быть положительной";
    public static final String NEGATIVE_TERM = "Срок кредита должен быть положительным";
    public static final String NEGATIVE_SALARY = "Зарплата должна быть положительной";
    public static final String NEGATIVE_DEPENDENT_AMOUNT = "Количество иждивенцев не может быть отрицательным";
    public static final String NEGATIVE_WORK_EXP_TOTAL = "Общий стаж работы не может быть отрицательным";
    public static final String NEGATIVE_WORK_EXP_CURRENT = "Стаж на текущем месте не может быть отрицательным";
    public static final String INVALID_EMPLOYERS_INN = "ИНН работодателя должен содержать 10 или 12 цифр";
    public static final String FUTURE_PASSPORT_ISSUE_DATE = "Дата выдачи паспорта не может быть в будущем";

}