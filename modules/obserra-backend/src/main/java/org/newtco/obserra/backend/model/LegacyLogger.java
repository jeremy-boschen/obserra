package org.newtco.obserra.backend.model;

/**
 * Model representing a logger and its configuration.
 * This corresponds to the logger information returned by the Spring Boot Actuator /loggers endpoint.
 */
public class LegacyLogger {

    private String        name;
    private ObLoggerLevel configuredLevel;
    private ObLoggerLevel effectiveLevel;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObLoggerLevel getConfiguredLevel() {
        return configuredLevel;
    }

    public void setConfiguredLevel(ObLoggerLevel configuredLevel) {
        this.configuredLevel = configuredLevel;
    }

    public ObLoggerLevel getEffectiveLevel() {
        return effectiveLevel;
    }

    public void setEffectiveLevel(ObLoggerLevel effectiveLevel) {
        this.effectiveLevel = effectiveLevel;
    }
}