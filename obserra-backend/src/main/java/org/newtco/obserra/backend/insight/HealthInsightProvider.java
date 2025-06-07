package org.newtco.obserra.backend.insight;

import java.util.LinkedHashMap;
import java.util.Map;

import org.newtco.obserra.backend.collector.config.properties.CollectionProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties;
import org.newtco.obserra.backend.model.HealthData;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
import org.newtco.obserra.shared.model.ui.HealthInsight;
import org.newtco.obserra.shared.model.ui.ServiceInsight;
import org.springframework.stereotype.Component;

/**
 * Provides insights about the health of a service.
 */
@Component
public class HealthInsightProvider implements ServiceInsightProvider<HealthInsight>, ServiceStatusProvider {

    private final SpringBootProperties.HealthProperties healthProperties;

    public HealthInsightProvider(CollectionProperties properties) {
        this.healthProperties = properties.springBoot().health();
    }

    @Override
    public String providerType() {
        return "health";
    }

    @Override
    public ServiceInsight<HealthInsight> provide(Service service) {
        var data = service.collectorData(providerType());
        if (data instanceof HealthData health) {
            return ServiceInsight.of(providerType(), new HealthInsight(health.status(),
                                                                       extractComponents(health)));

        }

        return ServiceInsight.of(providerType(), null);
    }

    @Override
    public ServiceStatus status(Service service) {
        var data = service.collectorData(providerType());
        if (data instanceof HealthData health) {
            return mapHealthStatus(health.status());
        }
        return ServiceStatus.UNKNOWN;
    }


    /**
     * Map Spring Boot health status to service status.
     *
     * @param healthStatus The health status from Spring Boot actuator
     *
     * @return The corresponding service status
     */
    private ServiceStatus mapHealthStatus(String healthStatus) {
        return switch (healthStatus.toUpperCase()) {
            case "UP" -> ServiceStatus.UP;
            case "DOWN" -> ServiceStatus.DOWN;
            case "OUT_OF_SERVICE" -> ServiceStatus.WARNING;
            default -> ServiceStatus.UNKNOWN;
        };
    }

    private Map<String, HealthInsight.HealthComponent> extractComponents(HealthData health) {
        if (healthProperties.showComponents()) {
            var components = new LinkedHashMap<String, HealthInsight.HealthComponent>();
            for (var component : health.components().entrySet()) {
                components.put(component.getKey(),
                               new HealthInsight.HealthComponent(component.getValue().status(),
                                                                 component.getValue().details()));
            }

            return components;
        }

        return Map.of();
    }
}
