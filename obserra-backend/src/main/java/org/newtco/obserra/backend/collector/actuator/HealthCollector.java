package org.newtco.obserra.backend.collector.actuator;

import java.util.Map;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.collector.CollectorUtils;
import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.HealthData;
import org.newtco.obserra.backend.model.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Collector for health status from the Spring Boot actuator health endpoint.
 */
@Component
public class HealthCollector implements ActuatorCollector<HealthProperties> {
    private static final Logger logger = LoggerFactory.getLogger(HealthCollector.class);

    private final RestClient       webClient;
    private final HealthProperties healthProperties;

    @Autowired
    public HealthCollector(CollectorConfig config) {
        this.healthProperties = config.properties().collectors().springBoot().health();
        this.webClient        = config.webClient();
    }

    @Nonnull
    @Override
    public String type() {
        return "health";
    }

    @Override
    public void collect(Service service, ActuatorEndpoint actuatorEndpoint) {
        logger.debug("Checking health for service: {} ({})", service.getName(), service.getId());

        var data = webClient.get()
            .uri(actuatorEndpoint.getHref())
            .retrieve()
            .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
            .body(HealthData.class);
        if (data == null) {
            data = new HealthData("UNKNOWN", Map.of());
        }

        logger.debug("Health check for service {} returned status: {}", service.getName(), data.status());

        service.collectorData(type(), data);
    }

    @Nonnull
    @Override
    public HealthProperties properties() {
        return healthProperties;
    }
}
