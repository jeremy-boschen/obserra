package org.newtco.obserra.backend.collector;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.model.ObService;

/**
 * Interface for collecting data about a service. Various Collector implementations will collect different types of
 * data, such as health checks, metrics, logs, etc.
 */
public interface Collector<P extends CollectorProperties> {

    /**
     * Returns the unique name of this collector
     */
    @Nonnull
    String name();

    /**
     * Get the type of data stored by this collector
     */
    @Nonnull
    Class<?> type();

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
