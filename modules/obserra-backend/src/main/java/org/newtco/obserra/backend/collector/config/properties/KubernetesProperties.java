package org.newtco.obserra.backend.collector.config.properties;

import java.time.Duration;

import org.newtco.obserra.backend.collector.CollectorProperties;
import org.newtco.obserra.backend.config.properties.CircuitBreakerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "obserra.collectors.kubernetes")
public record KubernetesProperties(
    Boolean enabled,
    Duration timeout,
    Duration checkInterval,
    CircuitBreakerProperties circuitBreaker) implements CollectorProperties {

    public KubernetesProperties() {
        this(null, null, null, null);
    }

    public KubernetesProperties {
        if (enabled == null) {
            enabled = CollectorProperties.super.enabled();
        }
        if (timeout == null) {
            timeout = CollectorProperties.super.timeout();
        }
        if (checkInterval == null) {
            checkInterval = CollectorProperties.super.checkInterval();
        }
        if (circuitBreaker == null) {
            circuitBreaker = new CircuitBreakerProperties();
        }
    }


    @Override
    public String toString() {
        return "{ " +
               "\"enabled\": " +
               enabled +
               ", " +
               "\"timeout\": " +
               timeout +
               ", " +
               "\"checkInterval\": " +
               checkInterval +
               ", " +
               "\"circuitBreaker\": " +
               circuitBreaker +
               " }";
    }
}
