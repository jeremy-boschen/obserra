package org.newtco.obserra.backend.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import org.newtco.obserra.backend.model.ObLog;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.backend.storage.Storage;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GraphQL data fetcher for logs.
 * This class provides GraphQL queries and subscriptions for logs using the Netflix DGS framework.
 */
@DgsComponent
public class LogDataFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(LogDataFetcher.class);

    private final Storage storage;
    
    // Map to store active log stream subscribers by service ID
    private final Map<String, FluxSink<List<ObLog>>> logStreamSubscribers = new ConcurrentHashMap<>();

    @Autowired
    public LogDataFetcher(Storage storage) {
        this.storage = storage;
    }

    /**
     * GraphQL query to get logs for a specific service.
     *
     * @param serviceId the service ID
     * @param limit the maximum number of logs to return
     * @return a list of logs for the specified service
     */
    @DgsQuery
    public List<ObLog> logs(@InputArgument String serviceId, @InputArgument Integer limit) {
        LOG.debug("GraphQL query: logs(serviceId: {}, limit: {})", serviceId, limit);
        
        // Validate service exists
        Optional<ObService> service = storage.getService(serviceId);
        if (service.isEmpty()) {
            LOG.warn("Service not found: {}", serviceId);
            return List.of();
        }
        
        // Get logs from storage
        return storage.getLogsForService(serviceId, limit != null ? limit : 100);
    }

    /**
     * GraphQL subscription for real-time log streaming.
     *
     * @param serviceId the service ID
     * @return a publisher that emits logs for the specified service
     */
    @DgsSubscription
    public Publisher<List<ObLog>> logStream(@InputArgument String serviceId) {
        LOG.debug("GraphQL subscription: logStream(serviceId: {})", serviceId);
        
        // Validate service exists
        Optional<ObService> service = storage.getService(serviceId);
        if (service.isEmpty()) {
            LOG.warn("Service not found for subscription: {}", serviceId);
            return Flux.empty();
        }
        
        // Create a Flux that will emit logs for the service
        return Flux.create(sink -> {
            // Store the sink for this service
            logStreamSubscribers.put(serviceId, sink);
            
            // Clean up when the subscription is cancelled
            sink.onCancel(() -> {
                LOG.debug("Log stream subscription cancelled for service: {}", serviceId);
                logStreamSubscribers.remove(serviceId);
            });
            
            // Initial data load
            List<ObLog> initialLogs = storage.getLogsForService(serviceId, 100);
            if (!initialLogs.isEmpty()) {
                sink.next(initialLogs);
            }
            
            // Set up a polling mechanism to check for new logs
            // In a production environment, this would be replaced with a more efficient event-based system
            Flux.interval(Duration.ofSeconds(5))
                .subscribe(tick -> {
                    List<ObLog> logs = storage.getLogsForService(serviceId, 100);
                    if (!logs.isEmpty()) {
                        sink.next(logs);
                    }
                });
        });
    }
    
    /**
     * Method to push new logs to active subscribers.
     * This method can be called by other components when new logs are available.
     *
     * @param serviceId the service ID
     * @param logs the new logs
     */
    public void pushLogsToSubscribers(String serviceId, List<ObLog> logs) {
        FluxSink<List<ObLog>> sink = logStreamSubscribers.get(serviceId);
        if (sink != null && !logs.isEmpty()) {
            sink.next(logs);
        }
    }
}