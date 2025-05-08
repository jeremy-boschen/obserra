package org.newtco.obserra.backend.model;

import java.util.Map;

/**
 * Model representing health data collected by a service.
 *
 * @param status
 * @param components
 */
public record HealthData(String status, Map<String, ComponentHealth> components) {
    public HealthData {
        if (status == null) {
            status = "UNKNOWN";
        }
        if (components == null) {
            components = Map.of();
        }
    }

    public record ComponentHealth(String status, Map<String, Object> details) {
        public ComponentHealth {
            if (status == null) {
                status = "UNKNOWN";
            }
            if (details == null) {
                details = Map.of();
            }
        }
    }
}
