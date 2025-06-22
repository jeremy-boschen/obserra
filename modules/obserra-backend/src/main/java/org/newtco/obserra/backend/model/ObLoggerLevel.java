package org.newtco.obserra.backend.model;

import java.util.List;

/**
 * Enum representing the possible log levels.
 * This corresponds to the standard log levels in Spring Boot.
 */
public enum ObLoggerLevel {
    OFF,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE;

    public static List<String> names() {
        return List.of("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");
    }
}