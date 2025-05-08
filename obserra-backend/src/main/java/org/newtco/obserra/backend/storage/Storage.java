package org.newtco.obserra.backend.storage;

import org.newtco.obserra.backend.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface for storage operations.
 * This provides a common interface for different storage implementations (in-memory, database, etc.)
 * 
 * The Storage interface is now used to persist a Service's data when necessary.
 * Services are responsible for storing their own data, and the Storage interface
 * is used to persist that data.
 */
public interface Storage {
    // User methods
    Optional<User> getUser(Long id);
    Optional<User> getUserByUsername(String username);
    User createUser(User user);

    // Service methods
    List<Service> getAllServices();
    Optional<Service> getService(Long id);
    Optional<Service> getServiceByPodName(String podName);
    Optional<Service> getServiceByAppId(String appId);
    Service createService(Service service);
    Service updateService(Long id, Service service);
    Service updateServiceStatus(Long id, ServiceStatus status);
    Service updateServiceLastSeen(Long id);
    void deleteService(Long id);

    // Service data persistence methods
    Service persistServiceData(Service service);

    // Service registration methods
    Service registerService(Service registration);

    // Health check methods
    List<Service> getServicesForHealthCheck(int maxAgeSeconds);

    // Metrics methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<Metric> getMetricsForService(Long serviceId, int limit);
    @Deprecated
    Metric createMetric(Metric metric);

    // Logs methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<Log> getLogsForService(Long serviceId, int limit);
    @Deprecated
    Log createLog(Log log);

    // Configuration methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<ConfigProperty> getConfigPropertiesForService(Long serviceId);
    @Deprecated
    Optional<ConfigProperty> getConfigProperty(Long id);
    @Deprecated
    ConfigProperty createConfigProperty(ConfigProperty property);
    @Deprecated
    ConfigProperty updateConfigProperty(Long id, ConfigProperty property);
    @Deprecated
    void deleteConfigProperty(Long id);
}
