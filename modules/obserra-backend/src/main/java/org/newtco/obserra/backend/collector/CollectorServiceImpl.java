package org.newtco.obserra.backend.collector;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.newtco.obserra.backend.collector.CircuitBreaker.DefaultFailureClassifier;
import org.newtco.obserra.backend.collector.config.properties.CollectionProperties;
import org.newtco.obserra.backend.config.properties.CircuitBreakerProperties;
import org.newtco.obserra.backend.core.concurrent.RunnableTaskScope;
import org.newtco.obserra.backend.core.concurrent.TaskScopeFactory;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.backend.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/// Service for collecting data from Collector instances. This service is responsible for running all collectors against
/// all registered services
///
/// ---
///
/// Calls to collectors are not guaranteed to be executed in any particular order, nor to be executed at all. If a
/// collection fails, then it will be retried according to its retry configuration or at the next scheduled interval for
/// all collections.
///
/// A maximum number of [Collector#collect] calls across all services is maintained. It can be increased via the
/// configuration setting `obserra.collection.max-concurrent-requests`.
@Component
@SuppressWarnings("preview")
public class CollectorServiceImpl implements CollectorService {
    private static final Logger logger = LoggerFactory.getLogger(CollectorServiceImpl.class);

    private final Storage                             storage;
    private final List<Collector<?>>                  collectors;
    private final StateManager                        stateManager;
    private final CollectionProperties                collectionProperties;
    private final Semaphore                           throttle;
    private final TaskScopeFactory<RunnableTaskScope> taskScopeFactory;
    private final Clock                               clock;

    // Original constructor maintained for backward compatibility
    @Autowired
    public CollectorServiceImpl(
        Storage storage,
        List<Collector<?>> collectors,
        CollectionProperties collectionProperties) {
        this(
            storage,
            collectors,
            collectionProperties,
            new StateManager(Clock.systemDefaultZone(), collectionProperties.springBoot().circuitBreaker()),
            new Semaphore(collectionProperties.maxConcurrentRequests()),
            new RunntableTaskScopeFactory(),
            Clock.systemDefaultZone()
        );
    }

    // visible for testing
    CollectorServiceImpl(
        Storage storage,
        List<Collector<?>> collectors,
        CollectionProperties collectionProperties,
        StateManager stateManager,
        Semaphore throttle,
        TaskScopeFactory<RunnableTaskScope> taskScopeFactory,
        Clock clock) {
        this.storage              = storage;
        this.collectors           = List.copyOf(collectors);
        this.stateManager         = stateManager;
        this.collectionProperties = collectionProperties;
        this.throttle             = throttle;
        this.taskScopeFactory     = taskScopeFactory;
        this.clock                = clock;
    }

    /**
     * Scheduled data collection for all services. This method is called periodically to collect data from all
     * registered services.
     */
    @Scheduled(fixedDelayString = "${obserra.collection.interval-ms:7000}")
    public void collectAllDataPeriodicallyInBackground() {
        var services = storage.getAllServices();
        logger.debug("Found {} services for background data collection", services.size());

        // Cleanup stale states
        stateManager.pruneInactiveServices(services);

        runServices(services);
    }

    /**
     * Used by a UI service to request immediate data collection for a specific service.
     *
     * @param service the service to collect data for
     */
    public void collectServiceDataNow(ObService service) {
        logger.debug("Running scheduled data collection for service {}", service.getName());

        //TODO: Services with their own timeouts
        try {
            runService(service, collectionProperties.timeout());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // visible for testing
    @Override
    public void runServices(List<ObService> services) {
        var deadline = clock.instant().plus(collectionProperties.timeout());

        try (var scope = taskScopeFactory.create("svc/")) {
            for (var service : services) {
                if (!stateManager.serviceState(service).isServiceEligible()) {
                    continue;
                }

                scope.fork(() -> {
                    //TODO: Services with their own timeouts
                    runService(service, collectionProperties.timeout());
                    return null;
                });
            }

            // Wait for all services to complete (or timeout)
            logger.debug("{} services eligible. Waiting for maximum of {}", services.size(), Duration.between(clock.instant(), deadline));
            scope.joinUntil(deadline);

            // Nothing else to do here. Service's will handle their own interruptions/timeouts

        } catch (TimeoutException e) {
            logger.error("Timed out while waiting for services", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // visible for testing
    @Override
    public void runService(ObService service, Duration timeout) throws InterruptedException {
        if (!stateManager.isEligible(service)) {
            logger.warn("Service {} is not eligible at this time", service.getName());
            return;
        }

        var deadline = clock.instant().plus(timeout);

        try (var scope = taskScopeFactory.create("svc/" + service.getId())) {
            var tasks = new ArrayList<Subtask<Void>>();
            for (var collector : collectors) {
                if (stateManager.isEligible(service, collector)) {
                    var task = scope.fork(() -> {
                        runCollector(service, collector);
                        return null;
                    });
                    tasks.add(task);
                } else {
                    logger.debug("Collector {} is not eligible at this time", service.getName());
                }
            }

            // Wait for all collectors of this service to complete
            scope.joinUntil(deadline);

            //TODO: This may be redundant if runCollector updates it
//            stateManager.onSuccess(service);
        } catch (InterruptedException e) {
            // Parent timed out
            //TODO: This may be redundant if runCollector updates it
            stateManager.onTimeout(service);

            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            //TODO: This may be redundant if runCollector updates it
            stateManager.onTimeout(service);
        }
    }

    @Override
    public void runCollector(ObService service, Collector<?> collector) throws InterruptedException {
        try {
            if (!throttle.tryAcquire(collectionProperties.timeout().toMillis(), TimeUnit.MILLISECONDS)) {
                // Other collectors are still running. This isn't an error for this collector, it just means
                // we ran out of time and don't want to hold everything up
                return;
            }

            try (var scope = taskScopeFactory.create("svc/" + service.getId() + "/col/" + collector.type())) {
                var task = scope.fork(() -> {
                    collector.collect(service);
                    return null;
                });

                var deadline = clock.instant().plus(collector.properties().timeout());
                scope.joinUntil(deadline);

                switch (task.state()) {
                    case UNAVAILABLE -> {
                        //TODO: For my use, when can this happen?
                    }
                    case SUCCESS -> // Update the service and collector
                        stateManager.onSuccess(service, collector);
                    case FAILED -> // Collector failed, update the service and collector
                        stateManager.onFailure(service, collector, task.exception());
                }
            } catch (TimeoutException e) {
                stateManager.onTimeout(service, collector);
            } catch (InterruptedException e) {
                // Parent scope was closed

                //TODO: Is there anything to update here?

                Thread.currentThread().interrupt();
            } finally {
                throttle.release();
            }
        } catch (InterruptedException e) {
            // Parent scope was closed
            logger.warn("Collector {}/{} was interrupted, likely by parent scope shutting down", service.getId(), collector.type());

            Thread.currentThread().interrupt();
            return;
        }
    }

    private static class RunntableTaskScopeFactory implements TaskScopeFactory<RunnableTaskScope> {

        @Override
        public RunnableTaskScope create(String name, ThreadFactory factory) {
            return new RunnableTaskScope();
        }
    }

    // visible for testing
    static class StateManager {
        private final Clock                     clock;
        private final CircuitBreakerProperties  config;
        private final Map<String, ServiceState> states;

        public StateManager(Clock clock, CircuitBreakerProperties config) {
            this.clock  = clock;
            this.config = config;
            this.states = new ConcurrentHashMap<>();
        }

        public boolean isEligible(ObService service) {
            return serviceState(service).isServiceEligible();
        }

        public boolean isEligible(ObService service, Collector<?> collector) {
            return serviceState(service).isCollectorEligible(collector);
        }

        public void onSuccess(ObService service) {
            serviceState(service).onSuccess();
        }

        public void onSuccess(ObService service, Collector<?> collector) {
            serviceState(service).onSuccess(collector);
        }

        public void onTimeout(ObService service) {
            serviceState(service).onTimeout();
        }

        public void onTimeout(ObService service, Collector<?> collector) {
            logger.warn("Service {} collector {} timed out", service.getName(), collector);
            serviceState(service).onTimeout(collector);
        }

        public void onFailure(ObService service, Collector<?> collector) {
            serviceState(service).onFailure(collector);
        }

        public void onFailure(ObService service, Collector<?> collector, Throwable error) {
            logger.error("Service {} collector {} failed: {}:{}", service.getName(), collector, error.getClass(), error.getMessage(), error);

            serviceState(service).onFailure(collector, error);
        }

        public void pruneInactiveServices(List<ObService> services) {
            var activeIds = services.stream()
                .map(ObService::getId)
                .collect(Collectors.toSet());

            states.keySet().removeIf(id -> !activeIds.contains(id));
        }

        // visible for testing
        ServiceState serviceState(ObService service) {
            return states.computeIfAbsent(service.getId(),
                                          serviceId -> new ServiceState(clock, serviceId + "/" + service.getName(), config));
        }
    }

    public static class ServiceFailureClassifier extends DefaultFailureClassifier {

    }

    ///
    private static class ServiceState {
        private final Clock                       clock;
        private final String                      stateId;
        private final CircuitBreaker              serviceBreaker;
        private final Map<String, CollectorState> collectorStates;

        public ServiceState(Clock clock, String stateId, CircuitBreakerProperties config) {
            this.clock           = clock;
            this.stateId         = stateId;
            this.serviceBreaker  = new CircuitBreaker(stateId,
                                                      clock,
                                                      config.baseDelay(),
                                                      config.maxDelay(),
                                                      config.maxBackoffExponent(),
                                                      config.failureCountThreshold(),
                                                      config.halfOpenSuccessCountThreshold(),
                                                      config.timeoutCountThreshold(),
                                                      new ServiceFailureClassifier());
            this.collectorStates = new ConcurrentHashMap<>();
        }

        String stateId() {
            return stateId;
        }

        public boolean isServiceEligible() {
            return serviceBreaker.tryAcquire();
        }

        public boolean isCollectorEligible(Collector<?> collector) {
            if (!isServiceEligible()) {
                return false;
            }

            return collectorState(collector).isEligible();
        }

        public void onSuccess() {
            serviceBreaker.onSuccess();
        }

        public void onSuccess(Collector<?> collector) {
            serviceBreaker.onSuccess();
            collectorState(collector).onSuccess();
        }

        public void onFailure() {
            serviceBreaker.onFailure();
        }

        public void onFailure(Collector<?> collector) {
            serviceBreaker.onFailure();
            collectorState(collector).onFailure();
        }

        public void onFailure(Collector<?> collector, Throwable error) {
            serviceBreaker.onFailure();
            collectorState(collector).onFailure(error);
        }

        public void onTimeout() {
            serviceBreaker.onTimeout();
        }

        public void onTimeout(Collector<?> collector) {
            serviceBreaker.onTimeout();
            collectorState(collector).onTimeout();
        }

        private CollectorState collectorState(Collector<?> collector) {
            return collectorStates.computeIfAbsent(stateId + "/" + collector.type(),
                                                   collectorKey -> new CollectorState(collectorKey,
                                                                                      collector.properties()));
        }

        private class CollectorState {
            private final String              stateId;
            private       Instant             nextAttempt;
            private       Instant             lastSuccess;
            private       int                 attempts;
            private       int                 failures;
            private final CollectorProperties properties;
            private final CircuitBreaker      breaker;

            CollectorState(String stateId, CollectorProperties config) {
                this.stateId     = stateId;
                this.nextAttempt = Instant.MIN;
                this.attempts    = 0;
                this.failures    = 0;
                this.lastSuccess = null;
                this.properties  = config;
                this.breaker     = new CircuitBreaker(stateId,
                                                      clock,
                                                      properties.circuitBreaker().failureCountThreshold(),
                                                      properties.circuitBreaker().halfOpenSuccessCountThreshold(),
                                                      properties.circuitBreaker().timeoutCountThreshold());
            }

            public String stateId() {
                return stateId;
            }

            /**
             * Whether the Collector is eligible to attempt data collection now.
             */
            public synchronized boolean isEligible() {
                if (breaker.tryAcquire()) {
                    if (clock.instant().isAfter(nextAttempt)) {
                        logger.debug("Collector {} is eligible", stateId);
                        return true;
                    } else {
                        logger.debug("Collector {} is NOT eligible for another {}", stateId, Duration.between(clock.instant(), nextAttempt));
                    }
                }
                return false;
            }

            /// Updates the next available attempt time for the collector.
            ///
            /// This method computes an exponential backoff (with jitter) delay for the individual collector retry,
            /// scheduling when the next collection attempt can be made.
            ///
            /// Note that this delay is maintained separately from the circuit breaker's state. While the circuit
            /// breaker governs overall service eligibility by tracking failures and timeouts at a global level, this
            /// method only controls the timing between successive collection attempts for a given collector.
            ///
            /// @param retriable whether the error is considered retriable, affecting the backoff behavior
            private void updateNextAvailableAttemptTime(boolean retriable) {
                if (retriable && ++attempts <= properties.retries()) {
                    // Exponential backoff: base_delay * (2^attempt) with optional jitter
                    long delayMillis = properties.retryDelay().toMillis() * (1L << (attempts - 1));
                    // Add jitter (±20%) to avoid thundering herd problems
                    double jitter = 0.8 + Math.random() * 0.4; // results in values between 0.8 and 1.2
                    delayMillis = (long) (delayMillis * jitter);

                    // Cap the max delay to avoid extreme waits
                    long maxDelayMillis = properties.checkInterval().toMillis() / 2;
                    delayMillis = Math.min(delayMillis, maxDelayMillis);

                    nextAttempt = clock.instant().plusMillis(delayMillis);
                    logger.debug("Scheduled retry #{} with delay of {}ms", attempts, delayMillis);
                } else {
                    nextAttempt = clock.instant().plus(properties.checkInterval());
                    attempts    = 0;
                }
            }


            public void onSuccess() {
                lastSuccess = clock.instant();
                failures    = 0;
                breaker.onSuccess();
                updateNextAvailableAttemptTime(false);
            }

            public void onFailure() {
                failures++;
                breaker.onFailure();
                updateNextAvailableAttemptTime(false);
            }

            public void onFailure(Throwable error) {
                // TODO: do something with error? Could be other form of timeout
                failures++;
                breaker.onFailure();
                updateNextAvailableAttemptTime(isRetriableError(error));
            }

            public void onTimeout() {
                failures++;
                breaker.onTimeout();
                updateNextAvailableAttemptTime(true);
            }

            private boolean isRetriableError(Throwable error) {
                if (error == null) {
                    return false;
                }

                if (error instanceof CollectionException ce) {
                    return ce.isRetriable();
                }

                return error instanceof ConnectException ||
                       error instanceof SocketTimeoutException ||
                       error instanceof ResourceAccessException;


            }

        }
    }
}