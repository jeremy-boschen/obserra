package org.newtco.obserra.backend.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.newtco.obserra.backend.status.NamedStatus;

/**
 * Model representing Spring Boot health actuator response for application/vnd.spring-boot.actuator.v2+json
 *
 * @param status  overall health status
 * @param details map of indicator name → its detail object
 */
public record HealthEndpointResponseV2(
    String status,
    Map<String, HealthDetail> details
) implements NamedStatus, HealthEndpointResponseV3.Compatible {
    public static final String TYPE    = "health";
    public static final String VERSION = "2";
    public static final String ACCEPT  = "application/vnd.spring-boot.actuator.v2+json";

    public HealthEndpointResponseV2 {
        if (status == null) {
            status = "UNKNOWN";
        }
        if (details == null) {
            details = Map.of();
        }
    }

    /**
     * Each entry under "details" has its own status and arbitrary fields.
     *
     * @param status  e.g. "UP"/"DOWN"
     * @param details arbitrary key–value props (total, free, threshold, etc.)
     */
    public record HealthDetail(
        String status,
        Map<String, Object> details
    ) {
        public HealthDetail {
            if (status == null) {
                status = "UNKNOWN";
            }
            if (details == null) {
                details = Map.of();
            }
        }
    }

    /** Convert *this* v2 response into the v3 model. */
    @Override
    public HealthEndpointResponseV3 toV3() {
        var components = details.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> new HealthEndpointResponseV3.ComponentHealth(
                    e.getValue().status(),
                    List.of(),                  // v2 has no nested components
                    e.getValue().details()
                )
            ));
        return new HealthEndpointResponseV3(status, components);
    }
}
