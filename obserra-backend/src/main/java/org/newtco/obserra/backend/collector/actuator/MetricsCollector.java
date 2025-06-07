package org.newtco.obserra.backend.collector.actuator;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.MetricsProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MetricsCollector implements ActuatorCollector<MetricsProperties> {
    private static final Logger logger = LoggerFactory.getLogger(MetricsCollector.class);

    private final RestClient        webClient;
    private final MetricsProperties metricsProperties;

    @Autowired
    public MetricsCollector(
        CollectorConfig config
    ) {
        this.metricsProperties = config.properties().collectors().springBoot().metrics();
        this.webClient         = config.webClient();
    }

    @Nonnull
    @Override
    public String type() {
        return "metrics";
    }

    @Override
    public void collect(Service service, ActuatorEndpoint endpoint) {

    }


    @Nonnull
    @Override
    public MetricsProperties properties() {
        return metricsProperties;
    }
}
