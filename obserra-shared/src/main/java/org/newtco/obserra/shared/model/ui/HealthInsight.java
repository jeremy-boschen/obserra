package org.newtco.obserra.shared.model.ui;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * UI model for health insights.
 *
 * @param status     Status of the service. One of "UP", "DOWN", "UNKNOWN", "OUT_OF_SERVICE"
 * @param components Map of component name to component health insights
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HealthInsight(
        String status,
        Map<String, HealthComponent> components
) {

    public HealthInsight {
        if (status == null) {
            status = "UNKNOWN";
        }
        if (components == null) {
            components = Map.of();
        }
    }

    /**
     * UI model for health component insights.
     *
     * @param status  Status of the component. One of "UP", "DOWN", "UNKNOWN", "OUT_OF_SERVICE"
     * @param details Map of detail name to detail value, specific to the component.
     */
    public record HealthComponent(String status, Map<String, Object> details) {
        public HealthComponent {
            if (status == null) {
                status = "UNKNOWN";
            }
            if (details == null) {
                details = Map.of();
            }
        }
    }
}
