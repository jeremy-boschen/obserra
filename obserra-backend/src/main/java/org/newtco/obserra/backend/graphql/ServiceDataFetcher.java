package org.newtco.obserra.backend.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.newtco.obserra.backend.insight.ServiceInsightProvider;
import org.newtco.obserra.backend.insight.ServiceStatusProvider;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
import org.newtco.obserra.backend.model.ui.UiService;
import org.newtco.obserra.backend.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * GraphQL data fetcher for service information.
 * This class provides GraphQL queries for service information using the Netflix DGS framework.
 */
@DgsComponent
public class ServiceDataFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceDataFetcher.class);

    private final Storage storage;
    private final List<ServiceInsightProvider<?>> insightProviders;
    private final List<ServiceStatusProvider> statusProviders;

    public ServiceDataFetcher(
            Storage storage,
            List<ServiceInsightProvider<?>> insightProviders,
            List<ServiceStatusProvider> statusProviders) {
        this.storage = storage;
        this.insightProviders = insightProviders;
        this.statusProviders = statusProviders;
    }

    /**
     * GraphQL query to get all services.
     *
     * @return a list of all services
     */
    @DgsQuery
    public List<UiService> services() {
        LOG.debug("GraphQL query: services");
        return storage.getAllServices().stream()
                .map(this::toUiService)
                .toList();
    }

    /**
     * GraphQL query to get a specific service by ID.
     *
     * @param id the service ID
     * @return the service with the specified ID, or null if not found
     */
    @DgsQuery
    public UiService service(@InputArgument String id) {
        LOG.debug("GraphQL query: service(id: {})", id);
        Optional<Service> service = storage.getService(id);
        return service.map(this::toUiService).orElse(null);
    }

    /**
     * Convert a Service object to a UiService object.
     * This method reuses the conversion logic from UiServiceController.
     *
     * @param service the Service object to convert
     * @return the converted UiService object
     */
    private UiService toUiService(Service service) {
        return new UiService(
                service.getId(),
                service.getAppId(),
                service.getName(),
                service.getVersion(),
                getServiceStatus(service),
                service.getLastUpdated(),
                insightProviders.stream()
                        .map(provider -> provider.provide(service))
                        .collect(
                                TreeMap::new,
                                (m, u) -> m.put(u.type(), u.insight()),
                                Map::putAll));
    }

    /**
     * Get the status of a service.
     * This method reuses the status determination logic from UiServiceController.
     *
     * @param service the Service object to get the status for
     * @return the status of the service
     */
    private ServiceStatus getServiceStatus(Service service) {
        for (ServiceStatusProvider provider : statusProviders) {
            var status = provider.status(service);
            if (status != ServiceStatus.UP) {
                return status;
            }
        }
        return ServiceStatus.UP;
    }
}