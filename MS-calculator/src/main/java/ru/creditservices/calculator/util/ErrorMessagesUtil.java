package ru.creditservices.calculator.util;

public final class ErrorMessagesUtil {

    private ErrorMessagesUtil() {}

    public static final String EMPTY_EMAIL = "Email должен быть указан";
    public static final String INVALID_EMAIL = "Некорректный формат email";

    public static final String EMPTY_PASSPORT_SERIES = "Серия паспорта должна быть указана";
    public static final String INVALID_PASSPORT_SERIES = "Серия паспорта должна содержать 4 цифры";

    public static final String EMPTY_PASSPORT_NUMBER = "Номер паспорта должен быть указан";
    public static final String INVALID_PASSPORT_NUMBER = "Номер паспорта должен содержать 6 цифр";

    public static final String EMPTY_PASSPORT_ISSUE_BRANCH = "Кем выдан паспорт должен быть заполнен";
    public static final String INVALID_PASSPORT_ISSUE_BRANCH = "Поле 'кем выдан' должно содержать от 3 до 100 символов";

    public static final String EMPTY_ACCOUNT_NUMBER = "Номер банковского счета должен быть указан";
    public static final String INVALID_ACCOUNT_NUMBER = "Номер банковского счета должен содержать 20 цифр";

    public static final String EMPTY_NAME = "Имя клиента должно быть указано";
    public static final String INVALID_NAME = "Имя клиента должно быть от 2 до 30 символов";

    public static final String EMPTY_LASTNAME = "Фамилия клиента должна быть указана";
    public static final String INVALID_LASTNAME = "Фамилия клиента должна быть от 2 до 30 символов";

    public static final String INVALID_MIDDLENAME = "Отчество клиента должно быть от 2 до 30 символов";

    public static final String EMPTY_BIRTHDATE = "Дата рождения клиента должна быть указана";

    public static final String EMPTY_AMOUNT = "Сумма кредита должна быть указана";
    public static final String NEGATIVE_AMOUNT = "Сумма кредита должна быть положительной";

    public static final String EMPTY_TERM = "Срок кредита должен быть указан";
    public static final String NEGATIVE_TERM = "Срок кредита должен быть положительным";

    public static final String EMPTY_EMPLOYMENT_STATUS = "Статус занятости не может быть пустым";
    public static final String EMPTY_MARITAL_STATUS = "Семейное положение должно быть указано";
    public static final String EMPTY_GENDER = "Пол клиента должен быть указан";

    public static final String EMPTY_SALARY = "Зарплата должна быть указана";
    public static final String NEGATIVE_SALARY = "Зарплата должна быть положительной";

    public static final String NEGATIVE_DEPENDENT_AMOUNT = "Количество иждивенцев не может быть отрицательным";

    public static final String INVALID_WORK_EXP_TOTAL = "Общий стаж работы должен быть указан";
    public static final String NEGATIVE_WORK_EXP_TOTAL = "Общий стаж работы не может быть отрицательным";

    public static final String INVALID_WORK_EXP_CURRENT = "Стаж работы на последнем месте должен быть указан";
    public static final String NEGATIVE_WORK_EXP_CURRENT = "Стаж на текущем месте не может быть отрицательным";

    public static final String EMPTY_EMPLOYERS_INN = "ИНН работодателя должен быть указан";
    public static final String INVALID_EMPLOYERS_INN = "ИНН работодателя должен содержать 10 или 12 цифр";

    public static final String EMPTY_PASSPORT_ISSUE_DATE = "Дата выдачи паспорта должна быть заполнена";
    public static final String FUTURE_PASSPORT_ISSUE_DATE = "Дата выдачи паспорта не может быть в будущем";
    public static final String EMPTY_INSURANCE_OPTION = "Опция страховки должна быть указана";
    public static final String EMPTY_SALARY_CLIENT_OPTION = "Статус зарплатного клиента должен быть указан";

    public static final String SCORING_BIRTHDATE_REQUIRED = "Дата рождения клиента должна быть указана";
    public static final String SCORING_INVALID_AGE = "Отказ: возраст заемщика должен быть от 20 до 65 лет";
    public static final String SCORING_EMPLOYMENT_REQUIRED = "Информация о трудоустройстве должна быть указана";
    public static final String SCORING_TOTAL_EXPERIENCE_REQUIRED = "Общий стаж работы должен быть указан";
    public static final String SCORING_TOTAL_EXPERIENCE_TOO_SMALL = "Отказ: общий стаж работы должен быть не менее 18 месяцев";
    public static final String SCORING_CURRENT_EXPERIENCE_REQUIRED = "Стаж работы на текущем месте должен быть указан";
    public static final String SCORING_CURRENT_EXPERIENCE_TOO_SMALL = "Отказ: стаж на текущем месте работы должен быть не менее 3 месяцев";
    public static final String SCORING_UNEMPLOYED = "Отказ: безработным клиентам кредит не выдается";
    public static final String SCORING_AMOUNT_OR_SALARY_REQUIRED = "Сумма кредита и зарплата должны быть указаны";
    public static final String SCORING_AMOUNT_TOO_LARGE = "Отказ: сумма кредита не должна превышать 24 зарплаты";

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
