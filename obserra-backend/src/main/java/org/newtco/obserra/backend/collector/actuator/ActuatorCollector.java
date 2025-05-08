package org.newtco.obserra.backend.collector.actuator;

import org.newtco.obserra.backend.collector.Collector;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.Service;

/**
 * Interface for collecting data from a specific type of actuator endpoint. Implementations of this interface will
 * handle different types of endpoints (metrics, logs, etc.)
 */
public interface ActuatorCollector<T> extends Collector<T> {

    /**
     * Get the type of endpoint this collector handles
     *
     * @return The endpoint type (e.g., "metrics", "health", "logfile")
     */
    String getType();

    /**
     * Collect data from a service using a specific actuator endpoint.
     *
     * @param service The service to collect data from
     * @param actuatorEndpoint The actuator endpoint to use for collection
     * @return A CollectorState containing the collected data and status
     */
    Collector.State<T> collect(Service service, ActuatorEndpoint actuatorEndpoint);

    @Override
    default Collector.State<T> collect(Service service) {
        var actuatorEndpoint = service.findActuatorEndpoint(getType())
                                      .orElse(null);

        if (null != actuatorEndpoint) {
            return collect(service, actuatorEndpoint);
        } else {
            return null;
        }
    }

    default boolean canCollect(ActuatorEndpoint endpoint) {
        return endpoint.getType().equals(getType());
    }
}
