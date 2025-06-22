package org.newtco.obserra.backend.collector;

import java.time.Duration;
import java.util.List;

import org.newtco.obserra.backend.model.ObService;

public interface CollectorService {
    /// Runs all collectors for the list of services
    ///
    /// @param services List of services to call collectors for
    void runServices(List<ObService> services);

    /// runs all collectors for the service
    ///
    /// @param service Service to call collectors for
    /// @param timeout Duration all collectors must complete in
    ///
    /// @throws InterruptedException Timeout occurred
    void runService(ObService service, Duration timeout) throws InterruptedException;

    /// Runs the collector for the service.
    ///
    /// @param service   The service
    /// @param collector The collector
    ///
    /// @throws InterruptedException timeout
    void runCollector(ObService service, Collector<?> collector) throws InterruptedException;
}
