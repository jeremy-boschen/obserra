package org.newtco.obserra.backend.collector.config.properties;


import java.time.Duration;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Represents collection properties used by the collector.
 *
 * @param interval              The interval at which to collect data, defaults to 5 seconds.
 * @param timeout               The maximum duration for all collection attempts, defaults to 60 seconds.
 * @param maxConcurrentRequests The maximum number of concurrent requests allowed. Defaults to 250.
 * @param springBoot            Spring Boot collector properties.
 * @param kubernetes            Kubernetes collector properties.
 */
@Validated
@EnableConfigurationProperties({
    SpringBootProperties.class,
    KubernetesProperties.class,
})
@ConfigurationProperties(prefix = "obserra.collectors")
public record CollectionProperties(
    Duration interval,
    Duration timeout,
    Integer maxConcurrentRequests,
    @NestedConfigurationProperty
    SpringBootProperties springBoot,
    @NestedConfigurationProperty
    KubernetesProperties kubernetes) {

    public CollectionProperties() {
        this(null, null, null, null, null);
    }

    public CollectionProperties {
        if (interval == null) {
            interval = Duration.ofSeconds(5);
        }
        if (timeout == null) {
            timeout = Duration.ofSeconds(60);
        }
        if (maxConcurrentRequests == null) {
            maxConcurrentRequests = 250;
        }
        if (springBoot == null) {
            springBoot = new SpringBootProperties();
        }
        if (kubernetes == null) {
            kubernetes = new KubernetesProperties();
        }
    }

    @PostConstruct
    public void validate() {
        if (!springBoot.enabled() && !kubernetes.enabled()) {
            throw new IllegalArgumentException("At least one collector must be enabled");
        }

        // Validate timeouts
        if (timeout.minus(springBoot.timeout()).isNegative()) {
            throw new IllegalArgumentException("obserra.collectors.spring-boot.timeout %s must be less than obserra.collectors.timeout %s"
                                                   .formatted(springBoot.timeout(), timeout));
        }

        if (timeout.minus(kubernetes.timeout()).isNegative()) {
            throw new IllegalArgumentException("obserra.collectors.kubernetes.timeout %s must be less than obserra.collectors.timeout %s"
                                                   .formatted(kubernetes.timeout(), timeout));
        }

        if (springBoot.timeout().minus(springBoot.health().timeout()).isNegative()) {
            throw new IllegalArgumentException("obserra.collectors.spring-boot.health.timeout %s must be less than obserra.collectors.spring-boot.timeout %s"
                                                   .formatted(springBoot.health().timeout(), springBoot.timeout()));
        }
    }
}