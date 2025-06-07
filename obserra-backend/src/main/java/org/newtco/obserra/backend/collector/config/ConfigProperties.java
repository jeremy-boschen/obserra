package org.newtco.obserra.backend.collector.config;


import org.newtco.obserra.backend.collector.config.properties.CollectionProperties;
import org.newtco.obserra.backend.collector.config.properties.WebClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        WebClientProperties.class,
        CollectionProperties.class
})
public class ConfigProperties {

    private final WebClientProperties  webClient;
    private final CollectionProperties collectors;

    public ConfigProperties(
            WebClientProperties webClient,
            CollectionProperties collectors
    ) {
        this.webClient  = webClient;
        this.collectors = collectors;
    }

    public WebClientProperties webClient() {
        return webClient;
    }

    public CollectionProperties collectors() {
        return collectors;
    }
}
