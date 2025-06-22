package org.newtco.obserra.backend.graphql;

import java.util.List;
import java.util.Optional;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.newtco.obserra.backend.insight.ServiceInsightProvider;
import org.newtco.obserra.backend.insight.ServiceStatusProvider;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.backend.storage.Storage;
import org.newtco.obserra.graphql.client.types.Service;
import org.newtco.obserra.graphql.client.types.ServiceInsights;
import org.newtco.obserra.graphql.client.types.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GraphQL data fetcher for service information. This class provides GraphQL queries for service information using the
 * Netflix DGS framework.
 */
@DgsComponent
public class ServiceDataFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceDataFetcher.class);

    private final Storage                         storage;
    private final List<ServiceInsightProvider<?>> insightProviders;
    private final List<ServiceStatusProvider>     statusProviders;

    public ServiceDataFetcher(
        Storage storage,
        List<ServiceInsightProvider<?>> insightProviders,
        List<ServiceStatusProvider> statusProviders) {
        this.storage          = storage;
        this.insightProviders = insightProviders;
        this.statusProviders  = statusProviders;
    }

    /**
     * GraphQL query to get all services.
     *
     * @return a list of all services
     */
    @DgsQuery
    public List<Service> services() {
        LOG.debug("GraphQL query: services");
        return storage.getAllServices().stream()
            .map(this::toService)
            .toList();
    }

    /**
     * GraphQL query to get a specific service by ID.
     *
     * @param id the service ID
     *
     * @return the service with the specified ID, or null if not found
     */
    @DgsQuery
    public Service service(@InputArgument String id) {
        LOG.debug("GraphQL query: service(id: {})", id);
        Optional<ObService> service = storage.getService(id);
        return service.map(this::toService).orElse(null);
    }

    /**
     * Convert a Service object to a UiService object. This method reuses the conversion logic from
     * UiServiceController.
     *
     * @param service the Service object to convert
     *
     * @return the converted UiService object
     */
    private Service toService(ObService service) {
        return new Service(
            service.getId(),
            service.getAppId(),
            service.getName(),
            service.getVersion(),
            service.getNamespace(),
            getServiceStatus(service),
            service.getLastUpdated(),
            getServiceInsights(service));
    }

    /**
     * Retrieves ObServiceInsight's from all registered ServiceInsightProviders and converts them into graphql insights
     * and finally constructs the ServiceInsights object
     */
    private ServiceInsights getServiceInsights(ObService service) {
        var insights = new ServiceInsights();
        for (var provider : insightProviders) {
            var insight = provider.provide(service);
            if (insight != null) {
                Handles.forServiceInsightField(provider.insightType())
                    .ifPresent(handle -> handle.set(insight));
            }
        }

        return insights;
    }

    /**
     * Get the status of a service.
     *
     * @param service the Service object to get the status for
     *
     * @return the status of the service
     *
     * @implNote Reports only {@link ServiceStatus#UP} if all status providers report {@link ServiceStatus#UP}.
     */
    private ServiceStatus getServiceStatus(ObService service) {
        for (ServiceStatusProvider provider : statusProviders) {
            var status = switch (provider.status(service)) {
                case UP -> ServiceStatus.UP;
                case DOWN, OUT_OF_SERVICE -> ServiceStatus.DOWN;
                case PENDING -> ServiceStatus.PENDING;
                case WARNING -> ServiceStatus.WARNING;
                case UNKNOWN -> ServiceStatus.UNKNOWN;
            };

            if (status != ServiceStatus.UP) {
                return status;
            }
        }

        return ServiceStatus.UP;
    }
}