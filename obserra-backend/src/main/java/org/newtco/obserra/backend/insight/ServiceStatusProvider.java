package org.newtco.obserra.backend.insight;

import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;

public interface ServiceStatusProvider {

    /**
     * Evaluate the collected data and determine the service status.
     *
     * @param service The service to determine the status for
     *
     * @return The service status determined by analyzing the data, or null if status should not be changed based on
     * this data
     */
    ServiceStatus status(Service service);
}
