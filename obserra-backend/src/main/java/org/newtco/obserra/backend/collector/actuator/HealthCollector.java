package org.newtco.obserra.backend.collector.actuator;

import org.newtco.obserra.backend.collector.Collector;
import org.newtco.obserra.backend.collector.StatusContributor;
import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
import org.newtco.obserra.backend.model.HealthData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

/**
 * Collector for health status from the Spring Boot actuator health endpoint.
 */
@Component
public class HealthCollector implements ActuatorCollector<HealthData>,
        StatusContributor<HealthData> {

    private static final Logger logger = LoggerFactory.getLogger(HealthCollector.class);

    private final RestClient       webClient;
    private final HealthProperties healthProperties;

    @Autowired
    public HealthCollector(
            CollectorConfig config) {
        this.healthProperties = config.properties().springBoot().health();
        this.webClient        = config.webClient();
    }


    @Override
    public String getType() {
        return "health";
    }

    @Override
    public Collector.State<HealthData> collect(Service service, ActuatorEndpoint actuatorEndpoint) {
        logger.debug("Checking health for service: {} ({})", service.getName(), service.getId());

        var response = webClient.get()
                                .uri(actuatorEndpoint.getHref())
                                .retrieve()
                                .body(HealthData.class);
        if (response == null) {
            response = new HealthData("UNKNOWN", Map.of());
        }

        logger.debug("Health check for service {} returned status: {}", service.getName(), response.status());

        return Collector.State.ofSuccess(response);
    }

    @Override
    public int getStatusPriority() {
        return 100;
    }

    @Override
    public ServiceStatus evaluateServiceStatus(HealthData data) {
        return Optional.ofNullable(data)
                       .map(HealthData::status)
                       .map(this::mapHealthStatus)
                       .orElse(ServiceStatus.UNKNOWN);
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
}
