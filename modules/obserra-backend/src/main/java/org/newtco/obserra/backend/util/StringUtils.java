package org.newtco.obserra.backend.util;

public class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String value) {
        return value != null && value.isEmpty();
    }

    public static boolean isBlank(String value) {
        return value != null && value.isBlank();
    }

    public static String lower(String value) {
        return value != null ? value.toLowerCase() : "";
    }

    public static String upper(String value) {
        return value != null ? value.toUpperCase() : "";
    }

    public static String capitalize(String value) {
        return value != null ? value.substring(0, 1).toUpperCase() + value.substring(1) : "";
    }
}
