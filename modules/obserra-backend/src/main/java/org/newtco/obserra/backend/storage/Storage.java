package org.newtco.obserra.backend.storage;

import java.util.List;
import java.util.Optional;

import org.newtco.obserra.backend.model.ConfigProperty;
import org.newtco.obserra.backend.model.ObLog;
import org.newtco.obserra.backend.model.Metric;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.backend.model.ObServiceStatus;
import org.newtco.obserra.backend.model.User;

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
    Optional<User> getUser(String id);
    Optional<User> getUserByUsername(String username);
    User createUser(User user);

    // Service methods
    List<ObService> getAllServices();
    Optional<ObService> getService(String id);
    Optional<ObService> getServiceByPodName(String podName);
    Optional<ObService> getServiceByAppId(String appId);
    ObService createService(ObService service);
    ObService updateService(String id, ObService service);
    ObService updateServiceByAppId(String appId, ObService service);
    ObService updateServiceStatus(String id, ObServiceStatus status);
    ObService updateServiceLastSeen(String id);
    void deleteService(String id);

    // Service data persistence methods
    ObService persistServiceData(ObService service);

    // Service registration methods
    ObService registerService(ObService registration);

    // Health check methods
    List<ObService> getServicesForHealthCheck(int maxAgeSeconds);

    // Metrics methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<Metric> getMetricsForService(String serviceId, int limit);
    @Deprecated
    Metric createMetric(Metric metric);

    // Logs methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<ObLog> getLogsForService(String serviceId, int limit);
    @Deprecated
    ObLog createLog(ObLog log);

    // Configuration methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<ConfigProperty> getConfigPropertiesForService(String serviceId);
    @Deprecated
    Optional<ConfigProperty> getConfigProperty(String id);
    @Deprecated
    ConfigProperty createConfigProperty(ConfigProperty property);
    @Deprecated
    ConfigProperty updateConfigProperty(String id, ConfigProperty property);
    @Deprecated
    void deleteConfigProperty(String id);
}
