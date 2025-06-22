package org.newtco.obserra.backend.model;

import java.util.List;
import java.util.Map;

/**
 * Data structure returned from Spring Boot actuator loggers endpoint
 *
 * @param levels
 * @param loggers
 * @param groups
 */
public record ObLoggers(
    List<String> levels,
    Map<String, ServiceLogger> loggers,
    Map<String, ServiceLogGroup> groups
) {

    public record ServiceLogger(String effectiveLevel, String configuredLevel) {
    }

    public record ServiceLogGroup(List<String> members) {
    }
}
