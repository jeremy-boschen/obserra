package org.newtco.obserra.backend.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Model representing a Spring Boot application service. This corresponds to the 'services' table in the schema.
 * <p>
 * The Service class is now responsible for storing its own data, separated into: 1. Runtime data required for service
 * interaction (id, name, status, etc.) 2. Collector data stored by collector instances (health, metrics, logs, etc.)
 */
public class Service {
    // Runtime data required for service interaction
    private String                 id;
    private String                 name;
    private String                 namespace          = "default";
    private String                 version            = "unknown";
    private String                 podName;
    private ServiceStatus          status             = ServiceStatus.UNKNOWN;
    private LocalDateTime          lastUpdated        = LocalDateTime.now();
    private LocalDateTime          lastSeen;
    private String                 clusterDns;
    private String                 actuatorUrl;
    private RegistrationSource     registrationSource = RegistrationSource.KUBERNETES;
    private String                 appId;
    private Boolean                autoRegister       = false;
    private Duration               checkInterval;
    private List<ActuatorEndpoint> actuatorEndpoints  = new ArrayList<>();
    private Map<String, Object>    collectorData      = new LinkedHashMap<>();

    // Collector data stored by collector instances
    private ServiceData serviceData = new ServiceData();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public Service setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Service setName(String name) {
        this.name = name;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public Service setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Service setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getPodName() {
        return podName;
    }

    public Service setPodName(String podName) {
        this.podName = podName;
        return this;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public Service setStatus(ServiceStatus status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Service setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public Service setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
        return this;
    }

    public String getClusterDns() {
        return clusterDns;
    }

    public Service setClusterDns(String clusterDns) {
        this.clusterDns = clusterDns;
        return this;
    }

    public String getActuatorUrl() {
        return actuatorUrl;
    }

    public Service setActuatorUrl(String actuatorUrl) {
        this.actuatorUrl = actuatorUrl;
        return this;
    }

    public RegistrationSource getRegistrationSource() {
        return registrationSource;
    }

    public Service setRegistrationSource(RegistrationSource registrationSource) {
        this.registrationSource = registrationSource;
        return this;
    }


    public String getAppId() {
        return appId;
    }

    public Service setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public Boolean getAutoRegister() {
        return autoRegister;
    }

    public Service setAutoRegister(Boolean autoRegister) {
        this.autoRegister = autoRegister;
        return this;
    }

    public Duration getCheckInterval() {
        return checkInterval;
    }

    public Service setCheckInterval(Duration checkInterval) {
        this.checkInterval = checkInterval;
        return this;
    }

    public List<ActuatorEndpoint> getActuatorEndpoints() {
        return actuatorEndpoints;
    }

    public Service setActuatorEndpoints(List<ActuatorEndpoint> actuatorEndpoints) {
        this.actuatorEndpoints = actuatorEndpoints;
        return this;
    }

    /**
     * Add an actuator endpoint to the list of available endpoints
     *
     * @param endpoint The actuator endpoint to add
     */
    public void addActuatorEndpoint(ActuatorEndpoint endpoint) {
        if (this.actuatorEndpoints == null) {
            this.actuatorEndpoints = new ArrayList<>();
        }
        this.actuatorEndpoints.add(endpoint);
    }

    /**
     * Find an actuator endpoint by its ID
     *
     * @param id The endpoint ID to find
     *
     * @return The actuator endpoint, or null if not found
     */
    public Optional<ActuatorEndpoint> findActuatorEndpoint(String id) {
        if (this.actuatorEndpoints == null) {
            return Optional.empty();
        }
        return this.actuatorEndpoints.stream()
                                     .filter(endpoint -> id.equals(endpoint.getType()))
                                     .findFirst()
                                     .filter(ActuatorEndpoint::isEnabled);
    }

    @SuppressWarnings("unchecked")
    public <T> T collectorData(String type) {
        return (T) collectorData.get(type);
    }

    public Service collectorData(String type, Object data) {
        this.collectorData.put(type, data);
        return this;
    }


    /**
     * Get the service data collected by collectors
     *
     * @return The service data
     */
    public ServiceData getServiceData() {
        return serviceData;
    }

    /**
     * Set the service data collected by collectors
     *
     * @param serviceData The service data
     *
     * @return This service instance for method chaining
     */
    public Service setServiceData(ServiceData serviceData) {
        this.serviceData = serviceData;
        return this;
    }

    public Service update(Service service) {
        this.id = service.id;
        this.name = service.name;
        this.namespace = service.namespace;
        this.version = service.version;
        this.podName = service.podName;
        this.status = service.status;
        this.lastUpdated = service.lastUpdated;
        this.lastSeen = service.lastSeen;
        this.clusterDns = service.clusterDns;
        return this;
    }
}
