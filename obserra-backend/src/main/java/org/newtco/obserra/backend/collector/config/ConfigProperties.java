package org.newtco.obserra.backend.collector.config;


import org.newtco.obserra.backend.collector.config.properties.KubernetesProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties;
import org.newtco.obserra.backend.collector.config.properties.WebClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        WebClientProperties.class,
        SpringBootProperties.class,
        KubernetesProperties.class
})
public class ConfigProperties {

    private final WebClientProperties  webClient;
    private final SpringBootProperties springBoot;
    private final KubernetesProperties kubernetes;

    public ConfigProperties(
            WebClientProperties webClient,
            SpringBootProperties springBoot,
            KubernetesProperties kubernetes
    ) {
        this.webClient = webClient;
        this.springBoot = springBoot;
        this.kubernetes = kubernetes;
    }

    public WebClientProperties webClient() {
        return webClient;
    }

    public SpringBootProperties springBoot() {
        return springBoot;
    }

    public KubernetesProperties kubernetes() {
        return kubernetes;
    }

}
