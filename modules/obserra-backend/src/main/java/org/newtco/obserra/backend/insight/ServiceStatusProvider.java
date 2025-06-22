package org.newtco.obserra.backend.insight;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.model.NamedStatus;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.backend.model.ObServiceStatus;

import static org.newtco.obserra.backend.util.StringUtils.upper;

public interface ServiceStatusProvider {

    @Nonnull
    String type();

    /**
     * Evaluate the collected data and determine the service status.
     *
     * @param service The service to determine the status for
     *
     * @return The service status determined by analyzing the data, or null if status should not be changed based on
     * this data
     */
    @Nonnull
    default ObServiceStatus status(ObService service) {
        if (service.collectorData(type()) instanceof NamedStatus data) {
            return switch (upper(data.status())) {
                case "UP" -> ObServiceStatus.UP;
                case "DOWN" -> ObServiceStatus.DOWN;
                case "PENDING" -> ObServiceStatus.PENDING;
                case "WARNING" -> ObServiceStatus.WARNING;
                case "OUT_OF_SERVICE" -> ObServiceStatus.OUT_OF_SERVICE;
                default -> ObServiceStatus.UNKNOWN;
            };
        }

        return ObServiceStatus.UNKNOWN;
    }
}
