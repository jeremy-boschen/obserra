package org.newtco.obserra.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.newtco.obserra.backend.insight.ServiceInsightProvider;
import org.newtco.obserra.backend.insight.ServiceStatusProvider;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
import org.newtco.obserra.backend.model.ui.UiService;
import org.newtco.obserra.backend.storage.Storage;
import org.newtco.obserra.shared.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UiServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(UiServiceController.class);

    private final Storage                         storage;
    private final List<ServiceInsightProvider<?>> insightProviders;
    private final List<ServiceStatusProvider>     statusProviders;

    public UiServiceController(
        Storage storage,
        List<ServiceInsightProvider<?>> insightProviders,
        List<ServiceStatusProvider> statusProviders) {
        this.storage          = storage;
        this.insightProviders = insightProviders;
        this.statusProviders  = statusProviders;
    }

    /**
     * Get all registered services.
     *
     * @return a list of all registered services
     */
    @GetMapping("/api/services")
    public ResponseEntity<?> getServices() {
        try {
            return ResponseEntity.ok(
                storage.getAllServices().stream()
                    .map(this::toUiService)
                    .toList()
            );
        } catch (Exception e) {
            LOG.error("Error fetching services", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to fetch services"));
        }
    }

    /**
     * Get a specific service by ID.
     *
     * @param id the service ID
     *
     * @return the service with the specified ID
     */
    @GetMapping("/api/services/{id}")
    public ResponseEntity<?> getService(@PathVariable String id) {
        try {
            Optional<Service> service = storage.getService(id);
            if (service.isPresent()) {
                return ResponseEntity.ok(service.map(this::toUiService).get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Service '%s' not found".formatted(id)));
            }
        } catch (Exception e) {
            LOG.error("Error fetching service {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to fetch service '%s'".formatted(id)));
        }
    }

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
