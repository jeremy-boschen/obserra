package org.newtco.obserra.backend.collector;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

/**
 * A thread-safe Circuit Breaker implementation that prevents repeated calls to a misbehaving downstream by cycling
 * through CLOSED → OPEN → HALF_OPEN states.  When tripped (OPEN), it schedules the next retry using exponential
 * back-off (baseDelayMs × 2^n) capped at maxDelayMs, with wide jitter (±50%).
 */
public class CircuitBreaker {
    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreaker.class);

    private final String            name;
    private final long              baseDelayMs;
    private final long              maxDelayMs;
    private final int               maxBackoffExponent;
    private final int               failureThreshold;
    private final int               halfOpenSuccessThreshold;
    private final int               maxTimeouts;
    private final FailureClassifier classifier;
    private final State             state;
    private final Clock             clock;

    /**
     * Constructs a CircuitBreaker with custom parameters.
     *
     * @param name                     name of the circuit breaker (for logging)
     * @param clock                    clock used for time
     * @param baseDelay                initial back-off delay
     * @param maxDelay                 maximum back-off delay
     * @param maxBackoffExponent       maximum exponent for back-off calculation
     * @param failureThreshold         number of hard failures to open the circuit
     * @param halfOpenSuccessThreshold successes in HALF_OPEN to close the circuit
     * @param timeoutCountThreshold    number of timeouts to open the circuit
     * @param classifier               exception classifier
     */
    public CircuitBreaker(String name,
                          Clock clock,
                          Duration baseDelay,
                          Duration maxDelay,
                          int maxBackoffExponent,
                          int failureThreshold,
                          int halfOpenSuccessThreshold,
                          int timeoutCountThreshold,
                          FailureClassifier classifier) {
        this.name                     = name;
        this.clock                    = clock;
        this.baseDelayMs              = baseDelay.toMillis();
        this.maxDelayMs               = maxDelay.toMillis();
        this.maxBackoffExponent       = maxBackoffExponent;
        this.failureThreshold         = failureThreshold;
        this.halfOpenSuccessThreshold = halfOpenSuccessThreshold;
        this.maxTimeouts              = timeoutCountThreshold;
        this.classifier               = classifier;
        this.state                    = new State();
        LOG.debug("{}: initialized (baseDelay={}ms, maxDelay={}ms, exp={}, failThr={}, timeoutThr={}, halfOpenSuccThr={})",
                  name, baseDelayMs, maxDelayMs, maxBackoffExponent, failureThreshold, maxTimeouts, halfOpenSuccessThreshold);
    }

    /**
     * Convenience constructor using defaults: 1s→30s back‐off, exp=6, built‐in classifier.
     */
    public CircuitBreaker(String name,
                          Clock clock,
                          int failureThreshold,
                          int halfOpenSuccessThreshold,
                          int maxTimeouts) {
        this(name,
             clock,
             Duration.ofSeconds(1),
             Duration.ofSeconds(30),
             6,
             failureThreshold,
             halfOpenSuccessThreshold,
             maxTimeouts,
             new DefaultFailureClassifier());
    }

    /**
     * Checks whether a call is permitted.
     *
     * @return true if allowed; false if still OPEN
     */
    public boolean tryAcquire() {
        boolean allowed = state.tryAcquire();
        LOG.trace("{}: tryAcquire → {} (state={})", name, allowed, state.status);
        return allowed;
    }

    /** Record a successful call. */
    public void onSuccess() {
        LOG.trace("{}: onSuccess()", name);
        state.recordSuccess();
    }

    /** Record a hard failure (non-timeout). */
    public void onFailure() {
        LOG.trace("{}: onFailure()", name);
        state.recordFailure();
    }

    /**
     * Classifies and records the given throwable.
     *
     * @param t the Throwable to classify
     */
    public void onThrowable(Throwable t) {
        FailureClassifier.FailureType type = classifier.classify(t);
        LOG.trace("{}: onThrowable({}) classified as {}", name, t.getClass().getSimpleName(), type);
        switch (type) {
            case SUCCESS -> onSuccess();
            case TIMEOUT -> onTimeout();
            case FAILURE -> onFailure();
            case IGNORED -> LOG.trace("{}: exception ignored", name);
        }
    }

    /** Record a timeout event. */
    public void onTimeout() {
        LOG.trace("{}: onTimeout()", name);
        state.recordTimeout();
    }

    /**
     * @return current circuit state
     */
    public String getState() {
        return state.status.name();
    }

    /**
     * Internal state machine for the circuit breaker.
     */
    private class State {
        private enum Status {CLOSED, OPEN, HALF_OPEN}

        private volatile Status        status            = Status.CLOSED;
        private final    AtomicInteger failureCount      = new AtomicInteger(0);
        private final    AtomicInteger timeoutCount      = new AtomicInteger(0);
        private final    AtomicInteger halfOpenSuccesses = new AtomicInteger(0);
        private volatile Instant       nextAttempt       = Instant.MIN;

        /** @see CircuitBreaker#tryAcquire() */
        public boolean tryAcquire() {
            if (status == Status.CLOSED) {
                return true;
            }
            if (status == Status.OPEN && clock.instant().isAfter(nextAttempt)) {
                status = Status.HALF_OPEN;
                LOG.trace("{}: transitioning OPEN → HALF_OPEN after back-off", name);
                return true;
            }
            return status == Status.HALF_OPEN;
        }

        /** Called when a probe call succeeds. */
        public void recordSuccess() {
            if (status == Status.HALF_OPEN) {
                int succ = halfOpenSuccesses.incrementAndGet();
                LOG.trace("{}: HALF_OPEN success #{}/{}", name, succ, halfOpenSuccessThreshold);
                if (succ >= halfOpenSuccessThreshold) {
                    close();
                }
            } else if (status == Status.CLOSED) {
                // normal reset
                close();
            }
        }

        /** Called when a hard failure occurs. */
        public void recordFailure() {
            if (status == Status.HALF_OPEN) {
                LOG.trace("{}: failure in HALF_OPEN → re-opening circuit", name);
                open();
                return;
            }
            int fails = failureCount.incrementAndGet();
            LOG.trace("{}: failure #{}/{}", name, fails, failureThreshold);
            if (fails >= failureThreshold) {
                LOG.trace("{}: failure threshold reached → opening circuit", name);
                open();
            }
        }

        /** Called when a timeout occurs. */
        public void recordTimeout() {
            if (status == Status.HALF_OPEN) {
                LOG.trace("{}: timeout in HALF_OPEN → re-opening circuit", name);
                open();
                return;
            }
            int to = timeoutCount.incrementAndGet();
            LOG.trace("{}: timeout #{}/{}", name, to, maxTimeouts);
            if (to >= maxTimeouts) {
                LOG.trace("{}: timeout threshold reached → opening circuit", name);
                open();
            }
        }

        /**
         * Trips circuit to OPEN, computes next retry, then resets counters.
         */
        private void open() {
            status = Status.OPEN;

            int exp    = Math.min(failureCount.get() + timeoutCount.get(), maxBackoffExponent);
            long raw   = baseDelayMs * (1L << exp);
            double factor = 0.5 + Math.random();  // ±50% jitter
            long delay  = Math.min((long)(raw * factor), maxDelayMs);
            nextAttempt = clock.instant().plusMillis(delay);

            LOG.trace("{}: OPEN (exp={}, raw={}ms, jitterFactor={}, delay={}ms); nextAttempt={}",
                     name, exp, raw, String.format("%.2f", factor), delay, nextAttempt);

            failureCount.set(0);
            timeoutCount.set(0);
            halfOpenSuccesses.set(0);
        }

        /** Resets to CLOSED state and clears counters. */
        private void close() {
            status = Status.CLOSED;
            failureCount.set(0);
            timeoutCount.set(0);
            halfOpenSuccesses.set(0);
            LOG.trace("{}: CLOSED (counters reset)", name);
        }
    }

    /** Default classifier treating I/O problems as timeouts. */
    public static class DefaultFailureClassifier implements FailureClassifier {
        @Override
        public FailureType classify(Throwable t) {
            if (t == null) {
                return FailureType.SUCCESS;
            }
            if (t instanceof ConnectException ||
                t instanceof SocketTimeoutException ||
                t instanceof ResourceAccessException) {
                return FailureType.TIMEOUT;
            }
            return FailureType.FAILURE;
        }
    }

    /** Classifies exceptions into SUCCESS, FAILURE, TIMEOUT, or IGNORED. */
    public interface FailureClassifier {
        enum FailureType {
            SUCCESS, FAILURE, TIMEOUT, IGNORED
        }

        FailureType classify(Throwable t);
    }
}
