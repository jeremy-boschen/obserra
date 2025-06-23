package org.newtco.obserra.backend.collector.actuator;

import java.util.Map;

import jakarta.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.newtco.obserra.backend.collector.CollectorUtils;
import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.LoggersProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.ObLoggerLevel;
import org.newtco.obserra.backend.model.ObLoggers;
import org.newtco.obserra.backend.model.ObService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LoggersCollector implements ActuatorCollector<LoggersProperties> {

    private static final Logger logger = LoggerFactory.getLogger(LoggersCollector.class);

    private final RestClient        webClient;
    private final LoggersProperties loggersProperties;

    @Autowired
    public LoggersCollector(
        CollectorConfig config
    ) {
        this.loggersProperties = config.properties().collectors().springBoot().loggers();
        this.webClient         = config.webClient();
    }

    @Nonnull
    @Override
    public String name() {
        return "loggers";
    }

    @Nonnull
    @Override
    public Class<?> collectedType() {
        return ObLoggers.class;
    }

    @Override
    public void collect(ObService service, ActuatorEndpoint endpoint) {
        logger.debug("Checking log levels for service: {} ({})", service.getName(), service.getId());

        var data = webClient.get()
            .uri(endpoint.getHref())
            .retrieve()
            .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
            .body(ObLoggers.class);
        if (data == null) {
            data = new ObLoggers(
                ObLoggerLevel.names(),
                Map.of(),
                Map.of()
            );
        }

        logger.debug("Loggers check for service {} returned {} loggers, {} groups",
                     service.getName(), data.loggers().size(), data.groups().size());

        service.updateCollectorData(collectedType(), data);
    }

    @NotNull
    @Override
    public LoggersProperties properties() {
        return loggersProperties;
    }
}
