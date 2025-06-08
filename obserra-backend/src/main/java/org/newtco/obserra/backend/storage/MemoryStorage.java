package org.newtco.obserra.backend.storage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.newtco.obserra.backend.model.ConfigProperty;
import org.newtco.obserra.backend.model.Log;
import org.newtco.obserra.backend.model.Metric;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
import org.newtco.obserra.backend.model.User;
import org.springframework.stereotype.Component;

/**
 * In-memory implementation of the Storage interface. This class stores all data in memory using Maps.
 */
@Component
public class MemoryStorage implements Storage {
    private final Map<String, User>                 users            = new ConcurrentHashMap<>();
    private final Map<String, Service>              services         = new ConcurrentHashMap<>();
    private final Map<String, List<Metric>>         metrics          = new ConcurrentHashMap<>();
    private final Map<String, List<Log>>            logs             = new ConcurrentHashMap<>();
    private final Map<String, List<ConfigProperty>> configProperties = new ConcurrentHashMap<>();

    private AtomicLong currentUserId           = new AtomicLong(1);
    private AtomicLong currentServiceId        = new AtomicLong(1);
    private AtomicLong currentMetricId         = new AtomicLong(1);
    private AtomicLong currentLogId            = new AtomicLong(1);
    private AtomicLong currentConfigPropertyId = new AtomicLong(1);

    // User methods
    @Override
    public Optional<User> getUser(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return users.values().stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst();
    }

    @Override
    public User createUser(User user) {
        user.setId(String.valueOf(currentUserId.getAndIncrement()));
        users.put(user.getId(), user);
        return user;
    }

    // Service methods
    @Override
    public List<Service> getAllServices() {
        return new ArrayList<>(services.values());
    }

    @Override
    public Optional<Service> getService(String id) {
        return Optional.ofNullable(services.get(id));
    }

    @Override
    public Optional<Service> getServiceByPodName(String podName) {
        return services.values().stream()
                       .filter(service -> podName.equals(service.getPodName()))
                       .findFirst();
    }

    @Override
    public Optional<Service> getServiceByAppId(String appId) {
        return services.values().stream()
                       .filter(service -> appId.equals(service.getAppId()))
                       .findFirst();
    }

    @Override
    public Service createService(Service service) {
        service.setLastUpdated(LocalDateTime.now());
        services.put(service.getId(), service);

        // Initialize empty lists for metrics and logs
        metrics.put(service.getId(), new ArrayList<>());
        logs.put(service.getId(), new ArrayList<>());
        configProperties.put(service.getId(), new ArrayList<>());

        return service;
    }

    @Override
    public Service updateService(String id, Service updatedService) {
        Service existingService = services.get(id);
        if (existingService == null) {
            throw new IllegalArgumentException("Service not found with id: " + id);
        }

        existingService.update(updatedService)
                .setLastSeen(LocalDateTime.MIN);

        return existingService;
    }

    @Override
    public Service updateServiceByAppId(String appId, Service service) {
        var existing = getServiceByAppId(appId);
        if (existing.isEmpty()) {
            return createService(service);
        }

        return existing.get().update(service)
            .setLastSeen(LocalDateTime.MIN);
    }

    @Override
    public Service updateServiceStatus(String id, ServiceStatus status) {
        Service service = services.get(id);
        if (service == null) {
            throw new IllegalArgumentException("Service not found with id: " + id);
        }

        service.setStatus(status);
        service.setLastUpdated(LocalDateTime.now());
        return service;
    }

    @Override
    public Service updateServiceLastSeen(String id) {
        Service service = services.get(id);
        if (service == null) {
            throw new IllegalArgumentException("Service not found with id: " + id);
        }

        service.setLastSeen(LocalDateTime.now());
        return service;
    }

    @Override
    public void deleteService(String id) {
        services.remove(id);
        metrics.remove(id);
        logs.remove(id);
        configProperties.remove(id);
    }

    @Override
    public Service persistServiceData(Service service) {
        // For MemoryStorage, this is a no-op since the service data is already stored in memory
        // In a database implementation, this would persist the service data to the database
        return service;
    }

    // Service registration methods
    @Override
    public Service registerService(Service registration) {
        // Check if this service has already registered with an appId
        Optional<Service> existingService = Optional.empty();

        if (registration.getAppId() != null) {
            existingService = getServiceByAppId(registration.getAppId());
        }

        // Update or create service
        if (existingService.isPresent()) {
            return updateService(existingService.get().getId(), registration);
        } else {
            // Generate a UUID if appId is not provided
            if (registration.getAppId() == null) {
                registration.setAppId(UUID.randomUUID().toString());
            }
            return createService(registration);
        }
    }

    // Health check methods
    @Override
    public List<Service> getServicesForHealthCheck(int maxAgeSeconds) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(maxAgeSeconds);

        return services.values().stream()
                       .filter(service -> {
                           // Skip services that don't need checking
                           if (service.getStatus() == ServiceStatus.DOWN &&
                               service.getLastSeen() != null &&
                               service.getLastSeen().isBefore(cutoffTime)) {
                               return false;
                           }

                           // Check based on interval if specified
                           if (service.getCheckInterval() != null && service.getLastSeen() != null) {
                               LocalDateTime nextCheckTime = service.getLastSeen()
                                                                    .plus(service.getCheckInterval());
                               return LocalDateTime.now().isAfter(nextCheckTime);
                           }

                           // Default: check services that haven't been checked recently
                           return service.getLastSeen() == null || service.getLastSeen().isBefore(cutoffTime);
                       })
                       .collect(Collectors.toList());
    }

    // Metrics methods
    @Override
    public List<Metric> getMetricsForService(String serviceId, int limit) {
        List<Metric> serviceMetrics = metrics.getOrDefault(serviceId, new ArrayList<>());

        // Return the most recent metrics first
        return serviceMetrics.stream()
                             .sorted(Comparator.comparing(Metric::getTimestamp).reversed())
                             .limit(limit)
                             .collect(Collectors.toList());
    }

    @Override
    public Metric createMetric(Metric metric) {
        metric.setId(String.valueOf(currentMetricId.getAndIncrement()));
        if (metric.getTimestamp() == null) {
            metric.setTimestamp(LocalDateTime.now());
        }

        List<Metric> serviceMetrics = metrics.computeIfAbsent(metric.getServiceId(), k -> new ArrayList<>());
        serviceMetrics.add(metric);

        return metric;
    }

    // Logs methods
    @Override
    public List<Log> getLogsForService(String serviceId, int limit) {
        List<Log> serviceLogs = logs.getOrDefault(serviceId, new ArrayList<>());

        // Return the most recent logs first
        return serviceLogs.stream()
                          .sorted(Comparator.comparing(Log::getTimestamp).reversed())
                          .limit(limit)
                          .collect(Collectors.toList());
    }

    @Override
    public Log createLog(Log log) {
        log.setId(String.valueOf(currentLogId.getAndIncrement()));
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }

        List<Log> serviceLogs = logs.computeIfAbsent(log.getServiceId(), k -> new ArrayList<>());
        serviceLogs.add(log);

        return log;
    }

    // Configuration methods
    @Override
    public List<ConfigProperty> getConfigPropertiesForService(String serviceId) {
        return configProperties.getOrDefault(serviceId, new ArrayList<>());
    }

    @Override
    public Optional<ConfigProperty> getConfigProperty(String id) {
        return configProperties.values().stream()
                               .flatMap(List::stream)
                               .filter(property -> property.getId().equals(id))
                               .findFirst();
    }

    @Override
    public ConfigProperty createConfigProperty(ConfigProperty property) {
        property.setId(String.valueOf(currentConfigPropertyId.getAndIncrement()));
        if (property.getLastUpdated() == null) {
            property.setLastUpdated(LocalDateTime.now());
        }

        List<ConfigProperty> serviceProperties = configProperties.computeIfAbsent(
                property.getServiceId(), k -> new ArrayList<>());
        serviceProperties.add(property);

        return property;
    }

    @Override
    public ConfigProperty updateConfigProperty(String id, ConfigProperty updatedProperty) {
        // Find the property to update
        for (List<ConfigProperty> properties : configProperties.values()) {
            for (int i = 0; i < properties.size(); i++) {
                ConfigProperty property = properties.get(i);
                if (property.getId().equals(id)) {
                    updatedProperty.setId(id);
                    updatedProperty.setLastUpdated(LocalDateTime.now());
                    properties.set(i, updatedProperty);
                    return updatedProperty;
                }
            }
        }

        throw new IllegalArgumentException("Config property not found with id: " + id);
    }

    @Override
    public void deleteConfigProperty(String id) {
        configProperties.values().forEach(properties -> {
            properties.removeIf(property -> property.getId().equals(id));
        });
    }
}
