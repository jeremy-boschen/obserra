package org.newtco.obserra.backend.collector;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.newtco.obserra.backend.collector.config.properties.CollectionProperties;
import org.newtco.obserra.backend.collector.config.properties.KubernetesProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.MetricsProperties;
import org.newtco.obserra.backend.config.properties.CircuitBreakerProperties;
import org.newtco.obserra.backend.core.concurrent.RunnableTaskScope;
import org.newtco.obserra.backend.core.concurrent.TaskScopeFactory;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.storage.Storage;

import static org.mockito.Mockito.*;

/**
 * Tests for CollectorService and related classes. Each test includes a rationale for why that behavior should be
 * verified.
 */
@ExtendWith(MockitoExtension.class)
class CollectorServiceTest {

    private CollectorServiceImpl                collectorService;
    private Storage                             storage;
    private CollectionProperties                collectionProperties;
    private Service                             service1;
    private Service                             service2;
    @Mock
    private Collector<CollectorProperties>      collector1;
    @Mock
    private Collector<CollectorProperties>      collector2;
    private CollectorServiceImpl.StateManager   stateManager;
    @Mock
    private TaskScopeFactory<RunnableTaskScope> taskScopeFactory;
    private Clock                               fixedClock;
    private Semaphore                           throttle;
    private RunnableTaskScope                   mockScope;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        // Mock dependencies
        storage    = mock(Storage.class);
        fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

        // Create properly initialized property records with spies
        // We create a complete hierarchy of property records with default values

        // Create SpringBootProperties with its nested properties
        SpringBootProperties springBootProps = new SpringBootProperties(
            true, // enabled
            Duration.ofSeconds(3), // timeout
            Duration.ofSeconds(10), // checkInterval
            new CircuitBreakerProperties(), // circuitBreaker
            new SpringBootProperties.HealthProperties(
                true, // enabled
                Duration.ofSeconds(2), // timeout
                Duration.ofSeconds(10), // checkInterval
                new CircuitBreakerProperties(), // circuitBreaker
                true // showComponents
            ),
            new MetricsProperties(
                true, // enabled
                Duration.ofSeconds(2), // timeout
                Duration.ofSeconds(10), // checkInterval
                new CircuitBreakerProperties() // circuitBreaker
            )
        );

        // Create CollectionProperties with the SpringBootProperties
        collectionProperties = spy(new CollectionProperties(
            Duration.ofSeconds(30), // interval
            Duration.ofSeconds(5), // timeout
            250, // maxConcurrentRequests
            springBootProps, // springBoot
            new KubernetesProperties() // kubernetes
        ));

        // Mock services
        service1 = mock(Service.class);
        lenient().when(service1.getName()).thenReturn("service1");
        lenient().when(service1.getId()).thenReturn("service1-id");
        service2 = mock(Service.class);
        lenient().when(service2.getName()).thenReturn("service2");
        lenient().when(service2.getId()).thenReturn("service2-id");

        // Mock collectors with their properties
        lenient().when(collector1.type()).thenReturn("health");
        lenient().when(collector1.properties()).thenReturn(springBootProps.health());

        lenient().when(collector2.type()).thenReturn("metrics");
        lenient().when(collector2.properties()).thenReturn(springBootProps.metrics());

        // Create StateManager with the real CircuitBreakerProperties
        stateManager = spy(new CollectorServiceImpl.StateManager(fixedClock, collectionProperties.springBoot().circuitBreaker()));

        // Create throttle with the configured max concurrent requests
        throttle = spy(new Semaphore(collectionProperties.maxConcurrentRequests()));

        // Mock task scope factory and scope
        mockScope = mock(RunnableTaskScope.class);
        when(taskScopeFactory.create(anyString())).thenReturn(mockScope);
        lenient().when(taskScopeFactory.create(anyString(), any())).thenReturn(mockScope);

        // Create the service under test
        collectorService = spy(new CollectorServiceImpl(
            storage,
            List.of(collector1, collector2),
            collectionProperties,
            stateManager,
            throttle,
            taskScopeFactory,
            fixedClock
        ));
    }

    @Test
    void testCollectAllDataPeriodicallyInBackground_schedulesAndInvokesRunServices() {
        // Rationale: ensure scheduled method actually calls runServices with the latest service list.
        // 1. Mock storage.getAllServices() to return service1 and service2
        // 2. Call collectAllDataPeriodicallyInBackground()
        // 3. Verify runServices() was called with the list of services
        // 4. Verify stateManager.pruneInactiveServices() was called with the list of services



        List<Service> services = List.of(service1, service2);
        when(storage.getAllServices()).thenReturn(services);

        collectorService.collectAllDataPeriodicallyInBackground();

        // Verify that both required methods are called with the correct services list
        verify(collectorService).runServices(services);
        verify(stateManager).pruneInactiveServices(services);
    }

    @Test
    void testCollectServiceDataNow_triggersImmediateCollectionForOneService() throws InterruptedException {
        // Rationale: UI-triggered immediate collect should delegate to runService with correct timeout.
        // 1. Stub collectorService.runService() to avoid real execution
        // 2. Call collectServiceDataNow(service1)
        // 3. Verify runService(service1, collectionProperties.timeout()) was called
        // 4. Verify the correct timeout was used
        collectorService.collectServiceDataNow(service1);
        verify(collectorService).runService(service1, collectionProperties.timeout());
    }

    @Test
    void testRunServices_filtersOutIneligibleServices() {
        // Rationale: services blocked by circuit breaker should not be polled again until healthy.
        // TODO:
        // 1. Configure stateManager to return true for service1.isEligible and false for service2.isEligible
        // 2. Spy on collectorService.runService() to track which services are processed
        // 3. Call runServices(List.of(service1, service2))
        // 4. Verify runService() was only called for service1 and not for service2
    }

    @Test
    void testRunService_timesOutWhenLongRunningCollector() throws InterruptedException {
        // Rationale: ensure that a collector exceeding the overall timeout is handled via interrupt.
        // TODO:
        // 1. Configure stateManager to return true for service1.isEligible and collector1.isEligible
        // 2. Configure mockScope.joinUntil() to throw TimeoutException
        // 3. Call runService(service1, Duration.ofMillis(10))
        // 4. Verify stateManager.onTimeout(service1) was called
    }

    @Test
    void testRunService_handlesInterruptedExceptionAndRecordsTimeout() throws InterruptedException {
        // Rationale: interrupts during collection should be caught and recorded as timeouts.
        // TODO:
        // 1. Configure stateManager to return true for service1.isEligible and collector1.isEligible
        // 2. Configure mockScope.joinUntil() to throw InterruptedException
        // 3. Call runService(service1, Duration.ofSeconds(1))
        // 4. Verify stateManager.onTimeout(service1) is called
        // 5. Verify the interrupted flag is set (may need to check Thread.currentThread().isInterrupted())
    }

    @Test
    void testRunCollector_invokesCollectorAndRecordsSuccess() throws InterruptedException {
        // Rationale: successful single-collector runs should record success in state manager.
        // TODO:
        // 1. Create a mock Subtask with SUCCESS state
        // 2. Configure mockScope.fork() to return this mock subtask
        // 3. Call runCollector(service1, collector1)
        // 4. Verify collector1.collect(service1) was called
        // 5. Verify stateManager.onSuccess(service1, collector1) was called
    }

    @Test
    void testRunCollector_handlesCollectorExceptionAndRecordsFailure() throws InterruptedException {
        // Rationale: exceptions during collection should be caught and recorded as failures.
        // TODO:
        // 1. Create a mock Subtask with FAILED state
        // 2. Configure the mock Subtask.exception() to return a RuntimeException
        // 3. Configure mockScope.fork() to return the mock Subtask
        // 4. Call runCollector(service1, collector1)
        // 5. Verify stateManager.onFailure(service1, collector1, exception) was called
    }

    @Test
    void testRunCollector_appliesThrottlingAcrossCollectors() throws InterruptedException {
        // Rationale: global maxConcurrentRequests must be enforced to protect target systems.
        // TODO:
        // 1. Set up a new Semaphore with 1 permit
        // 2. Spy on the semaphore's tryAcquire() method
        // 3. Create a new CollectorServiceImpl with this semaphore
        // 4. Call runCollector() twice in sequence
        // 5. Verify tryAcquire() was called twice and release() was called twice
    }

    @Test
    void testRunCollector_handlesTimeoutExceptionAndRecordsTimeout() throws InterruptedException {
        // Rationale: timeouts during collection should be caught and recorded.
        // TODO:
        // 1. Configure mockScope.joinUntil() to throw TimeoutException
        // 2. Call runCollector(service1, collector1)
        // 3. Verify stateManager.onTimeout(service1, collector1) was called
        // 4. Verify throttle was released
    }

    @Test
    void testStateManager_transitionsOnSuccessThenFailure() {
        // Rationale: circuit breaker state transitions must follow configured thresholds.
        // TODO:
        // 1. Create a CircuitBreakerProperties with failureCountThreshold=2
        // 2. Create a new StateManager with fixed clock and these properties
        // 3. Call stateManager.onSuccess(service1)
        // 4. Verify service1 is eligible
        // 5. Call stateManager.onFailure(service1, collector1) twice
        // 6. Verify service1 is now ineligible
    }

    @Test
    void testStateManager_prunesInactiveServices() {
        // Rationale: obsolete service states should be cleaned up to prevent memory leaks.
        // TODO:
        // 1. Call stateManager.onSuccess(service1) and stateManager.onSuccess(service2)
        // 2. Call stateManager.pruneInactiveServices with only service1 in the list
        // 3. Verify service1 is still eligible
        // 4. Verify service2 is not in the state map anymore
    }

    @Test
    void testCollectionProperties_defaultsAndValidation() {
        // Rationale: default property values and validation logic drive overall service behavior.
        // TODO:
        // 1. Create a new CollectionProperties() with null fields
        // 2. Verify default interval is 30s
        // 3. Verify default timeout is 5s
        // 4. Verify default maxConcurrentRequests is 250
        // 5. Create SpringBootProperties and KubernetesProperties with enabled=false
        // 6. Create a new CollectionProperties with these disabled sub-properties
        // 7. Call collectionProperties.validate() and verify it throws IllegalArgumentException
    }
}
