package org.newtco.obserra.backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatUtils {

    private FormatUtils() {
    }


    public static String formatMemory(Double bytes) {
        if (bytes == null) {
            return "N/A";
        }

        // Convert bytes to the appropriate unit (KB, MB, GB)
        String[] units     = {"B", "KB", "MB", "GB", "TB"};
        int      unitIndex = 0;
        double   value     = bytes;

        while (value > 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }

        // Format with at most 2 decimal places
        var formatted = String.format("%.2f", value);
        if (formatted.endsWith(".00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }
        return formatted + " " + units[unitIndex];
    }

    public static String formatDuration(Long milliseconds) {
        if (milliseconds == null) {
            return "N/A";
        }

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours   = minutes / 60;
        long days    = hours / 24;

        hours %= 24;
        minutes %= 60;
        seconds %= 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            result.append(minutes).append("m ");
        }
        result.append(seconds).append("s");

        return result.toString();
    }

    public static String formatTimestamp(Long milliseconds) {
        if (milliseconds == null) {
            return "N/A";
        }

        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new java.util.Date(milliseconds));
    }

    public static String formatDiskSpace(Double bytes) {
        if (bytes == null) {
            return "N/A";
        }

        // Convert bytes to appropriate unit (KB, MB, GB)
        String[] units     = {"B", "KB", "MB", "GB", "TB"};
        int      unitIndex = 0;
        double   value     = bytes;

        while (value > 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }

        // Format with at most 2 decimal places
        return String.format("%.2f %s", value, units[unitIndex]);
    }

    public static String formatCount(Double count) {
        if (count == null) {
            return "N/A";
        }
        return Integer.toString(count.intValue());
    }

    public static String formatQuantity(Double quantity) {
        if (quantity == null) {
            return "N/A";
        }

        long queueSize = quantity.longValue();
        if (queueSize < 1000) {
            return Long.toString(queueSize);
        } else if (queueSize < 1000_000) {
            return String.format("%.1fK", queueSize / 1000.0);
        } else {
            return String.format("%.1fM", queueSize / 1_000_000.0);
        }
    }
}
