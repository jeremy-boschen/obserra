package org.newtco.obserra.backend.collector.actuator;

import org.newtco.obserra.backend.collector.Collector;
import org.newtco.obserra.backend.collector.CollectorProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.Service;

/**
 * Interface for collecting data from a specific type of actuator endpoint. Implementations of this interface will
 * handle different types of endpoints (metrics, logs, etc.)
 */
public interface ActuatorCollector<P extends CollectorProperties> extends Collector<P> {

    /**
     * Collect data from a service using a specific actuator endpoint.
     *
     * @param service  The service to collect data from
     * @param endpoint The actuator endpoint to use for collection
     */
    void collect(Service service, ActuatorEndpoint endpoint) ;

    @Override
    default void collect(Service service)  {
        var endpoint = service.findActuatorEndpoint(type())
                              .orElseThrow(() -> new IllegalArgumentException("No actuator endpoint of type " + type() + " found for service " + service.getName()));
        collect(service, endpoint);
    }

    default boolean canCollect(ActuatorEndpoint endpoint) {
        return endpoint.getType().equals(type());
    }
}
