package org.newtco.obserra.backend.collector.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "obserra.collectors.spring-boot")
public record SpringBootProperties(
        Boolean enabled,
        Duration timeout,
        Duration checkInterval,
        HealthProperties health) implements CollectorProperties {

    public SpringBootProperties {
        if (enabled == null) {
            enabled = CollectorProperties.super.enabled();
        }
        if (timeout == null) {
            timeout = CollectorProperties.super.timeout();
        }
        if (checkInterval == null) {
            checkInterval = CollectorProperties.super.checkInterval();
        }
    }

    /**
     * Health collector configuration
     */
    public record HealthProperties(
            Boolean enabled,
            Duration timeout,
            Duration checkInterval,
            @DefaultValue("false") boolean showComponents) implements CollectorProperties {


        public HealthProperties {
            if (enabled == null) {
                enabled = CollectorProperties.super.enabled();
            }
            if (timeout == null) {
                timeout = CollectorProperties.super.timeout();
            }
            if (checkInterval == null) {
                checkInterval = CollectorProperties.super.checkInterval();
            }
        }
    }
}
