package org.newtco.obserra.backend.collector.actuator;

import org.newtco.obserra.backend.collector.Collector;
import org.newtco.obserra.backend.collector.CollectorException;
import org.newtco.obserra.backend.collector.CollectorProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.ObService;

/**
 * Extension to the Collector interface for collecting data from a specific Spring Boot actuator endpoint.
 * Implementations of this interface will handle different types of endpoints (metrics, logs, etc.)
 * <p>
 * The name() method is expected to match the Spring Boot Endpoint ID of the actuator it handles. For example, the
 * HealthCollector name is "health", and the MetricsCollector name is "metrics".
 */
public interface ActuatorCollector<P extends CollectorProperties> extends Collector<P> {

    /**
     * Collect data from a service using a specific actuator endpoint.
     *
     * @param service  The service to collect data from
     * @param endpoint The actuator endpoint to use for collection
     */
    void collect(ObService service, ActuatorEndpoint endpoint);

    /**
     * Collects data from the specified service. This method locates the appropriate actuator endpoint for the service
     * by matching the collector's name to the endpoint type. If no suitable endpoint is found, an exception is thrown.
     * Once the endpoint is retrieved, data collection is delegated.
     *
     * @param service The service from which to collect data.
     *
     * @throws CollectorException if no matching actuator endpoint is found for the service.
     */
    @Override
    default void collect(ObService service) {
        var endpoint = service.findActuatorEndpoint(name())
            .orElseThrow(() -> new CollectorException("No actuator endpoint of type " + name() + " found for service " + service.getName()));
        collect(service, endpoint);
    }

    /**
     * Determines if this collector can handle data collection for the specified actuator endpoint.
     *
     * @param endpoint the actuator endpoint to check, representing a specific type of actuator functionality.
     *                 Typically, contains metadata such as type, URL, and other related properties.
     *
     * @return true if the type of the given endpoint matches the name of this collector, indicating it can handle data
     * collection for that endpoint; false otherwise. If true, the collection service will call the collector to collect
     * its data.
     */
    default boolean canCollectForEndpoint(ActuatorEndpoint endpoint) {
        return name().equals(endpoint.getType());
    }
}
