package org.newtco.obserra.backend.collector.config.properties;

import java.time.Duration;

import org.newtco.obserra.backend.collector.CollectorProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.LoggersProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.MetricsProperties;
import org.newtco.obserra.backend.config.properties.CircuitBreakerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/// Represents the Spring Boot configuration properties for the Obserra Collector. This class configures various aspects
/// of the collector, including its enabled state, timeout durations, check intervals, and circuit breaker settings. It
/// also includes health and metrics configurations.
///
/// Constructs a new SpringBootProperties record with default values.
///
/// @param enabled        The default enabled state for the collector. Defaults to true if not specified.
/// @param timeout        The default timeout duration for health checks and other operations. Defaults to 30 seconds if
///                       not specified.
/// @param checkInterval  The default interval at which health checks are performed. Defaults to 5 seconds if not
///                       specified.
/// @param circuitBreaker The default CircuitBreakerProperties configuration. Defaults to a new CircuitBreakerProperties
///                       object if not specified.
/// @param health         The default HealthProperties configuration. Defaults to a new HealthProperties object if not
///                       specified.
/// @param metrics        The default MetricsProperties configuration. Defaults to a new MetricsProperties object if not
///                       specified.
@ConfigurationProperties(prefix = "obserra.collectors.spring-boot")
public record SpringBootProperties(
    Boolean enabled,
    Duration timeout,
    Duration checkInterval,
    @NestedConfigurationProperty
    CircuitBreakerProperties circuitBreaker,
    @NestedConfigurationProperty
    HealthProperties health,
    @NestedConfigurationProperty
    MetricsProperties metrics,
    @NestedConfigurationProperty
    LoggersProperties loggers
    ) implements CollectorProperties {

    /// Constructs a new SpringBootProperties record with default values.
    public SpringBootProperties() {
        this(null, null, null, null, null, null, null);
    }

    /// Constructs a new SpringBootProperties record with the provided values.
    ///
    /// @param enabled        The enabled state for the collector.
    /// @param timeout        The timeout duration for health checks and other operations.
    /// @param checkInterval  The interval at which health checks are performed.
    /// @param circuitBreaker The CircuitBreakerProperties configuration.
    /// @param health         The HealthProperties configuration.
    /// @param metrics        The MetricsProperties configuration.
    public SpringBootProperties {
        if (enabled == null) {
            enabled = true;
        }
        if (timeout == null) {
            timeout = Duration.ofSeconds(30);
        }
        if (checkInterval == null) {
            checkInterval = Duration.ofSeconds(5);
        }
        if (circuitBreaker == null) {
            circuitBreaker = new CircuitBreakerProperties();
        }
        if (health == null) {
            health = new HealthProperties();
        }
        if (metrics == null) {
            metrics = new MetricsProperties();
        }
        if (loggers == null) {
            loggers = new LoggersProperties();
        }
    }

    /// Health collector configuration. This record defines properties related to health checks, such as timeout
    /// duration and check interval.
    ///
    /// @param enabled        The default enabled state for health checks. Defaults to true if not specified.
    /// @param timeout        The default timeout duration for health checks. Defaults to 5 seconds if not specified.
    /// @param checkInterval  The default interval at which health checks are performed. Defaults to 10 seconds if not
    ///                       specified.
    /// @param circuitBreaker The default CircuitBreakerProperties configuration. Defaults to a new
    ///                       CircuitBreakerProperties object if not specified.
    /// @param showComponents Whether to display components in the health check results. Defaults to false if not
    ///                       specified.
    public record HealthProperties(
        Boolean enabled,
        Duration timeout,
        Duration checkInterval,
        @NestedConfigurationProperty
        CircuitBreakerProperties circuitBreaker,
        Boolean showComponents) implements CollectorProperties {

        /// Constructs a new HealthProperties record with default values.
        public HealthProperties() {
            this(null, null, null, null, false);
        }

        /// Constructs a new HealthProperties record with the provided values.
        ///
        /// @param enabled        The enabled state for health checks.
        /// @param timeout        The timeout duration for health checks.
        /// @param checkInterval  The interval at which health checks are performed.
        /// @param circuitBreaker The CircuitBreakerProperties configuration.
        /// @param showComponents Whether to display components in the health check results.
        public HealthProperties {

            if (enabled == null) {
                enabled = true;
            }
            if (timeout == null) {
                timeout = Duration.ofSeconds(5);
            }
            if (checkInterval == null) {
                checkInterval = Duration.ofSeconds(10);
            }
            if (circuitBreaker == null) {
                circuitBreaker = new CircuitBreakerProperties();
            }
            if (showComponents == null) {
                showComponents = false;
            }
        }

        /// Metrics Properties configuration.
        ///
        /// @param enabled        The enabled state for metrics collection. Defaults to true if not specified.
        /// @param timeout        The default timeout duration for metric operations. Defaults to 5 seconds if not
        ///                       specified.
        /// @param checkInterval  The interval at which metrics are collected. Defaults to 1 minute if not specified.
        /// @param circuitBreaker The CircuitBreakerProperties configuration. Defaults to a new CircuitBreakerProperties
        ///                       object if not specified.
        public record MetricsProperties(
            Boolean enabled,
            Duration timeout,
            Duration checkInterval,
            @NestedConfigurationProperty
            CircuitBreakerProperties circuitBreaker) implements CollectorProperties {

            /// Constructs a new MetricsProperties record with default values.
            public MetricsProperties() {
                this(null, null, null, null);
            }

            /// Constructs a new MetricsProperties record with the provided values.
            ///
            /// @param enabled        The enabled state for metrics collection.
            /// @param timeout        The default timeout duration for metric operations.
            /// @param checkInterval  The interval at which metrics are collected.
            /// @param circuitBreaker The CircuitBreakerProperties configuration.
            public MetricsProperties {
                if (enabled == null) {
                    enabled = true;
                }
                if (timeout == null) {
                    timeout = Duration.ofSeconds(30);
                }
                if (checkInterval == null) {
                    checkInterval = Duration.ofMinutes(1);
                }
                if (circuitBreaker == null) {
                    circuitBreaker = new CircuitBreakerProperties();
                }
            }
        }

        public record LoggersProperties(
            Boolean enabled,
            Duration timeout,
            Duration checkInterval,
            @NestedConfigurationProperty
            CircuitBreakerProperties circuitBreaker) implements CollectorProperties {
            public LoggersProperties() {
                this(null, null, null, null);
            }

            public LoggersProperties {
                if (enabled == null) {
                    enabled = true;
                }
                if (timeout == null) {
                    timeout = Duration.ofSeconds(30);
                }
                if (checkInterval == null) {
                    checkInterval = Duration.ofMinutes(1);
                }
                if (circuitBreaker == null) {
                    circuitBreaker = new CircuitBreakerProperties();
                }
            }
        }
    }
}