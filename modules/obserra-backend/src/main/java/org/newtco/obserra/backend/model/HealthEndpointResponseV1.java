package org.newtco.obserra.backend.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.newtco.obserra.backend.status.NamedStatus;

/**
 * Model representing Spring Boot health actuator response for application/vnd.spring-boot.actuator.v1+json
 * <p>
 * V1 inlines each indicator under the root alongside "status", so we capture them into a map of name → detail.
 *
 * @param status     overall health status
 * @param components map of indicator name → its detail object
 */
public record HealthEndpointResponseV1(
    String status,
    Map<String, ComponentHealth> components
) implements NamedStatus, HealthEndpointResponseV3.Compatible {
    public static final String TYPE    = "health";
    public static final String VERSION = "1";
    public static final String ACCEPT  = "application/vnd.spring-boot.actuator.v1+json";

    public HealthEndpointResponseV1 {
        if (status == null) {
            status = "UNKNOWN";
        }
        if (components == null) {
            components = Map.of();
        }
    }

    /**
     * Represents one root‐level indicator in the v1 payload.
     *
     * @param status  e.g. "UP"/"DOWN"
     * @param details all other props (total, free, database, etc.)
     */
    public record ComponentHealth(
        String status,
        Map<String, Object> details
    ) {
        public ComponentHealth {
            if (status == null) {
                status = "UNKNOWN";
            }
            if (details == null) {
                details = Map.of();
            }
        }
    }

    /** Convert *this* v1 response into the v3 model. */
    @Override
    public HealthEndpointResponseV3 toV3() {
        var comps = components.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> new HealthEndpointResponseV3.ComponentHealth(
                    e.getValue().status(),
                    List.of(),                  // no nested components in v1
                    e.getValue().details()
                )
            ));
        return new HealthEndpointResponseV3(status, comps);
    }
}
