package ru.creditservices.statement.util;

public class RegexPatternsUtil {
    private RegexPatternsUtil() {}

    public static final String EMAIL =
            "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$";
    public static final String PASSPORT_SERIES = "^\\d{4}$";
    public static final String PASSPORT_NUMBER = "^\\d{6}$";
    public static final String NAME = "^[A-Za-z]{2,30}$";
}
