package org.newtco.obserra.backend.collector;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.model.ObService;

/**
 * Interface for collecting data about a service. Collector implementations collect different types of data, such as
 * health checks, metrics, logs, etc. The ObService class has methods for storing and retrieving collected data based on
 * the collected type.
 * <p>
 * This interface is generic in how it collects data. Specializations exist for Spring Boot actuator endpoints,
 * kubernetes services, etc.
 */
public interface Collector<P extends CollectorProperties> {

    /**
     * Returns the unique name of this collector. This can map to an arbitrary value or something more meaningful like
     * the name of a remote endpoint.
     */
    @Nonnull
    String name();

    /**
     * Get the type of data stored by this collector. What is stored should be coordinated with an InsightProvider,
     * which will take collected data in this format and convert it to the outbound GraphQL insight type.
     */
    @Nonnull
    Class<?> collectedType();

    /**
     * Collect data from a service. The implementation of this method should know what it's collecting and how.
     *
     * @param service The service to collect data from
     */
    void collect(ObService service);

    /**
     * Get the properties for this collector.
     */
    @Nonnull
    P properties();
}
