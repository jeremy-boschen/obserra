package org.newtco.obserra.backend.model;

import java.util.List;
import java.util.Map;

import org.newtco.obserra.backend.status.NamedStatus;

/// Model representing Spring Boot health endpoint response structure for application/vnd.spring-boot.actuator.v3+json
///
///
/// @param status
/// @param components
///
/// @see <a
/// href="https://docs.spring.io/spring-boot/api/rest/actuator/health.html#health.retrieving.response-structure">Health
/// Response Structure</a>
public record HealthEndpointResponseV3(
    String status,
    Map<String, ComponentHealth> components
) implements NamedStatus {
    public static final String TYPE    = "health";
    public static final String VERSION = "3";
    public static final String ACCEPT  = "application/vnd.spring-boot.actuator.v3+json";

    public HealthEndpointResponseV3 {
        if (status == null) {
            status = "UNKNOWN";
        }
        if (components == null) {
            components = Map.of();
        }
    }

    public record ComponentHealth(String status, List<ComponentHealth> components, Map<String, Object> details) {
        public ComponentHealth {
            if (status == null) {
                status = "UNKNOWN";
            }
            if (components == null) {
                components = List.of();
            }
            if (details == null) {
                details = Map.of();
            }
        }
    }

    public interface Compatible {
        HealthEndpointResponseV3 toV3();
    }
}
