package org.newtco.obserra.backend.collector.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "obserra.collectors.web-client")
public record WebClientProperties(Duration connectTimeout, Duration readTimeout) {

    public WebClientProperties(
            @DefaultValue("5s") Duration connectTimeout,
            @DefaultValue("5s") Duration readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout    = readTimeout;
    }
}
