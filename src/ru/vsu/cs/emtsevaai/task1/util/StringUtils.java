package ru.vsu.cs.emtsevaai.task1.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}