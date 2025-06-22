package org.newtco.obserra.backend.collector.config;


import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CollectorConfig {


    private final ConfigProperties properties;
    private final RestClient       webClient;

    public CollectorConfig(
            ConfigProperties properties,
            RestClient.Builder webClientBuilder
    ) {
        this.properties = properties;
        this.webClient  = webClientBuilder.
                requestFactory(ClientHttpRequestFactoryBuilder.detect().build(
                        ClientHttpRequestFactorySettings.defaults()
                                                        .withConnectTimeout(properties.webClient().connectTimeout())
                                                        .withReadTimeout(properties.webClient().readTimeout())))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public ConfigProperties properties() {
        return properties;
    }

    public RestClient webClient() {
        return webClient;
    }
}