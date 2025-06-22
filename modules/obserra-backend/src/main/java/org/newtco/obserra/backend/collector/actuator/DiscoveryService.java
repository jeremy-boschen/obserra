package org.newtco.obserra.backend.collector.actuator;

import java.util.List;
import java.util.Map;

import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.ObService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

/**
 * Service for discovering available actuator endpoints from a Spring Boot application. This service queries the
 * actuator endpoint to discover available endpoints, accepting each for which there is an available ActuatorCollector.
 */
@Component("actuatorDiscoveryService")
public class DiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryService.class);

    private final RestClient                 webClient;
    private final List<ActuatorCollector<?>> collectors;

    @Autowired
    public DiscoveryService(
        CollectorConfig config,
        List<ActuatorCollector<?>> collectors) {

        this.webClient  = config.webClient();
        this.collectors = collectors;
    }

    /**
     * Discover available actuator endpoints for a service
     *
     * @param service The service to discover endpoints for
     *
     * @return A list of discovered actuator endpoints
     */
    public List<ActuatorEndpoint> discoverServiceEndpoints(ObService service) {
        logger.debug("Discovering Spring Boot actuator endpoints for service: {} ({})", service.getName(), service.getId());

        try {
            var actuatorLinks = webClient.get()
                .uri(service.getActuatorUrl())
                .retrieve()
                .body(ActuatorLinks.class);

            if (null != actuatorLinks) {
                return actuatorLinks._links().entrySet().stream()
                    .map(entry -> new ActuatorEndpoint()
                        .setType(entry.getKey())
                        .setHref(entry.getValue().getHref())
                        .setTemplated(entry.getValue().isTemplated())
                        .setEnabled(true))
                    .filter(this::isSupportedActuator)
                    .toList();
            }
        } catch (HttpStatusCodeException e) {
            logger.error("Error discovering Spring Boot actuator endpoints for service {}: {}", service.getName(), e.getMessage(), e);
        }

        return List.of();
    }

    private boolean isSupportedActuator(ActuatorEndpoint endpoint) {
        for (var collector : collectors) {
            if (collector.canCollect(endpoint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * POJO for the actuator links response
     */
    private record ActuatorLinks(Map<String, Link> _links) {

        public ActuatorLinks {
            if (_links == null) {
                _links = Map.of();
            }
        }
    }
}