package org.newtco.obserra.backend.collector.actuator;

import java.util.Map;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.collector.CollectorUtils;
import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.HealthEndpointResponseV1;
import org.newtco.obserra.backend.model.HealthEndpointResponseV2;
import org.newtco.obserra.backend.model.HealthEndpointResponseV3;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.backend.model.Platform;
import org.newtco.obserra.backend.util.Tuple;
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
    private static final Logger log = LoggerFactory.getLogger(HealthCollector.class);

    private final RestClient       webClient;
    private final HealthProperties healthProperties;

    @Autowired
    public HealthCollector(CollectorConfig config) {
        this.healthProperties = config.properties().collectors().springBoot().health();
        this.webClient        = config.webClient();
    }

    @Nonnull
    @Override
    public String name() {
        // Must match HealthEndpoint.ID.value
        return "health";
    }

    @Nonnull
    @Override
    public Class<?> collectedType() {
        return HealthEndpointResponseV3.class;
    }

    @Override
    public void collect(ObService service, ActuatorEndpoint endpoint) {
        log.debug("Checking health for service: {} ({})", service.getName(), service.getId());

        // Switch the Accept request header and the response format depending on the SpringBoot version of the
        // service we're calling
        var type = switch (service.getPlatform()) {
            case Platform p when p.matches(p.ofVersion("1.0+")) -> Tuple.of(
                HealthEndpointResponseV1.class, HealthEndpointResponseV1.ACCEPT);

            case Platform p when p.matches(p.ofVersion("2+")) -> Tuple.of(
                HealthEndpointResponseV2.class, HealthEndpointResponseV2.ACCEPT);

            default -> Tuple.of(
                HealthEndpointResponseV3.class, HealthEndpointResponseV3.ACCEPT);
        };

        var data = webClient.get()
            .uri(endpoint.getHref())
            // Match the response format this supports
            .header("Accept", type.second())
            .retrieve()
            .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
            .body(type.first());
        if (data == null) {
            data = new HealthEndpointResponseV3("UNKNOWN", Map.of());
        }

        log.debug("Health check for service {} returned status: {}", service.getName(), data.status());

        service.updateCollectorData(HealthEndpointResponseV3.class,
                                    data instanceof HealthEndpointResponseV3.Compatible vX ? vX.toV3() : data);
    }

    @Nonnull
    @Override
    public HealthProperties properties() {
        return healthProperties;
    }
}
