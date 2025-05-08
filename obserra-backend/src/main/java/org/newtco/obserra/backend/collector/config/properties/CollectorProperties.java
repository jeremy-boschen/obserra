package org.newtco.obserra.backend.collector.config.properties;

import java.time.Duration;

public interface CollectorProperties {

    default Boolean enabled() {
        return true;
    }

    default Duration timeout() {
        return Duration.ofSeconds(5);
    }

    default Duration checkInterval() {
        return Duration.ofSeconds(30);
    }

    /**
     * Number of retries to perform when collecting data from a service.     *
     */
    default Integer retries() {
        return 3;
    }

    /**
     * Delay between retries when collecting data from a service. An exponential backoff is used for each retry.
     */
    default Duration retryDelay() {
        return Duration.ofSeconds(10);
    }
}
