package org.newtco.obserra.backend.storage;

import java.util.List;
import java.util.Optional;

import org.newtco.obserra.backend.model.ConfigProperty;
import org.newtco.obserra.backend.model.Log;
import org.newtco.obserra.backend.model.Metric;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
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
    List<Service> getAllServices();
    Optional<Service> getService(String id);
    Optional<Service> getServiceByPodName(String podName);
    Optional<Service> getServiceByAppId(String appId);
    Service createService(Service service);
    Service updateService(String id, Service service);
    Service updateServiceStatus(String id, ServiceStatus status);
    Service updateServiceLastSeen(String id);
    void deleteService(String id);

    // Service data persistence methods
    Service persistServiceData(Service service);

    // Service registration methods
    Service registerService(Service registration);

    // Health check methods
    List<Service> getServicesForHealthCheck(int maxAgeSeconds);

    // Metrics methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<Metric> getMetricsForService(String serviceId, int limit);
    @Deprecated
    Metric createMetric(Metric metric);

    // Logs methods - deprecated, use Service.getServiceData() instead
    @Deprecated
    List<Log> getLogsForService(String serviceId, int limit);
    @Deprecated
    Log createLog(Log log);

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
