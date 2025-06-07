package org.newtco.obserra.backend.config.properties;


import java.time.Duration;

/// Configuration properties for implementing a Circuit Breaker pattern in distributed systems. This record encapsulates
/// all parameters needed to control the behavior of the circuit breaker, such as initial delays, maximum delays, retry
/// exponentiation, failure thresholds, and timeout handling. The Circuit Breaker pattern is used to prevent cascading
/// failures by automatically resetting failed connections after a specified number of attempts. The properties in this
/// class define various strategies for managing these attempts:
/// - `baseDelay`: Starting delay duration before the first retry.
/// - `maxDelay`: Maximum allowable delay before failing the request and breaking the circuit.
/// - `maxBackoffExponent`: Exponent used to calculate retry delays (e.g., doubles the delay each time).
/// - `failureCountThreshold`: Number of failures allowed before the circuit is broken permanently.
/// - `halfOpenSuccessCountThreshold`: Number of successful attempts needed to keep the circuit open.
/// - `timeoutCountThreshold`: Number of consecutive timeout events to trigger a circuit break.
///
/// The constructor provides default values for all parameters, making it easy to create a configuration without
/// explicitly setting each value.
public record CircuitBreakerProperties(
    Duration baseDelay,
    Duration maxDelay,
    Integer maxBackoffExponent,
    Integer failureCountThreshold,
    Integer halfOpenSuccessCountThreshold,
    Integer timeoutCountThreshold
) {
    public CircuitBreakerProperties() {
        this(null, null, null, null, null, null);
    }

    public CircuitBreakerProperties {
        if (baseDelay == null) {
            baseDelay = Duration.ofSeconds(1);
        }

        if (maxDelay == null) {
            maxDelay = Duration.ofSeconds(30);
        }

        if (maxBackoffExponent == null) {
            maxBackoffExponent = 6;
        }

        if (failureCountThreshold == null) {
            failureCountThreshold = 5;
        }

        if (halfOpenSuccessCountThreshold == null) {
            halfOpenSuccessCountThreshold = 3;
        }

        if (timeoutCountThreshold == null) {
            timeoutCountThreshold = 3;
        }
    }
}
