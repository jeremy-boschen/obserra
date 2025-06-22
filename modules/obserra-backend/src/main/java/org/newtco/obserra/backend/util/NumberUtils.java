package org.newtco.obserra.backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    private NumberUtils() {
    }

    public static Integer intValue(Double value) {
        return value != null ? value.intValue() : null;
    }

    public static int intValue(Double value, int defaultValue) {
        return value != null ? value.intValue() : defaultValue;
    }

    public static Long longValue(Double value) {
        return value != null ? value.longValue() : null;
    }

    public static long longValue(Double value, long defaultValue) {
        return value != null ? value.longValue() : defaultValue;
    }

    public static Double percentageOf(Double part, Double whole, int scale) {
        if (part == null || whole == null) {
            return null;
        }

        return BigDecimal.valueOf((part / whole) * 100)
            .setScale(scale, RoundingMode.HALF_UP)
            .doubleValue();
    }

    public static Double percentageOf(Double percentage, int scale) {
        if (percentage == null) {
            return null;
        }

        return BigDecimal.valueOf(percentage * 100)
            .setScale(scale, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
