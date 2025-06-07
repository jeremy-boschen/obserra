package org.newtco.obserra.backend.collector;

import java.time.Duration;

import org.newtco.obserra.backend.config.properties.CircuitBreakerProperties;

public interface CollectorProperties {

    /**
     * Whether this collector is enabled.
     */
    default Boolean enabled() {
        return true;
    }

    /**
     * Timeout for collecting data from a service.
     */
    default Duration timeout() {
        return Duration.ofSeconds(5);
    }

    /**
     * Interval between collection attempts.
     */
    default Duration checkInterval() {
        return Duration.ofSeconds(60);
    }

    /**
     * Number of retries to perform when collecting data from a service.
     */
    default Integer retries() {
        return 3;
    }

    /**
     * Delay between retries when collecting data from a service. An exponential backoff is used for each retry.
     */
    default Duration retryDelay() {
        return Duration.ofSeconds(5);
    }

    /**
     * Circuit breaker properties for this collector.
     */
    default CircuitBreakerProperties circuitBreaker() {
        return new CircuitBreakerProperties();
    }
}
