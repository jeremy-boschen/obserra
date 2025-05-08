package org.newtco.obserra.backend.collector;

import org.newtco.obserra.backend.model.ServiceStatus;

/**
 * Interface for collectors that can contribute to service status. Extends the base Collector interface with status
 * evaluation capabilities.
 */
public interface StatusContributor<T> {

    /**
     * Get the priority of this collector for status determination. Higher priority collectors can override lower
     * priority ones.
     *
     * @return The priority level (higher means more important)
     */
    int getStatusPriority();

    /**
     * Evaluate the collected data and determine the service status.
     *
     * @param data The data collected by this collector
     *
     * @return The service status determined by analyzing the data, or null if status should not be changed based on
     * this data
     */
    ServiceStatus evaluateServiceStatus(T data);

    interface Priority {
        // Lower priority means more important
        int HEALTH  = -1;
    }
}