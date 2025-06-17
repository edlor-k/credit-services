package ru.creditservices.calculator.util;

public final class RegexPatternsUtil {

    private RegexPatternsUtil() {}

    public static final String EMAIL =
            "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$";
    public static final String PASSPORT_SERIES = "^\\d{4}$";
    public static final String PASSPORT_NUMBER = "^\\d{6}$";
    public static final String ACCOUNT_NUMBER = "^\\d{20}$";
    public static final String INN = "^\\d{10}|\\d{12}$"; // 10 или 12 цифр
    public static final String NAME = "^[A-Za-z]{2,30}$";
}
