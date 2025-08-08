package ru.creditservices.deal.util;

import java.security.SecureRandom;

public class SesCodeGeneratorUtil {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SesCodeGeneratorUtil() {
    }

    public static String generateSesCode() {
        StringBuilder sb = new StringBuilder(DEFAULT_CODE_LENGTH);
        for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }
}
