package org.newtco.bootmonitoring;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;

import org.newtco.obserra.shared.model.ServiceRegistration;
import org.newtco.obserra.shared.model.ServiceRegistration.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Service for interacting with the monitoring backend
 * <p>
 * This service provides methods for registering the application with the monitoring backend and sending custom
 * metrics.
 */
public class MonitorService {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorService.class);

    private final String                      registrationServer;
    private final RestTemplate                restTemplate;
    private final Duration                    updateInterval;
    private       String                      registrationId;
    private       ServiceRegistration.Request registration;
    private       ScheduledExecutorService    scheduler;

    /**
     * Constructor
     *
     * @param properties Monitor configuration properties
     */
    public MonitorService(RestTemplateBuilder restTemplateBuilder, MonitorProperties properties) {
        this.restTemplate       = restTemplateBuilder.build();
        this.registrationServer = properties.getRegistrationServer() + (properties.getRegistrationServer().endsWith("/")
                                                                        ? "" : "/");
        this.updateInterval     = properties.getUpdateInterval();
    }

    /**
     * Register an application with the backend manually using a ServiceRegistrationRequest
     * <p>
     * This method can be used if auto-registration is disabled or if you need to re-register the application.
     *
     * @param registration ServiceRegistrationRequest with application information
     */
    public void registerWithBackend(ServiceRegistration.Request registration) {
        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            var response = restTemplate.postForObject(
                registrationServer + "api/registration/service",
                new HttpEntity<>(registration, headers),
                ServiceRegistration.Response.class);

            if (response != null && response.getRegistrationId() != null) {
                registrationId = response.getRegistrationId();
            } else {
                LOG.error("Failed to register with monitoring backend: no registrationId returned");
            }
        } catch (Exception e) {
            LOG.error("Failed to register with monitoring backend", e);
        }

        if (updateInterval != null && !updateInterval.isZero() && !updateInterval.isNegative()) {
            scheduleUpdate(registration);
        }
    }

    private synchronized void scheduleUpdate(Request registration) {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        this.registration = registration;

        scheduler.schedule(this::scheduledRegistration, updateInterval.toMillis(), TimeUnit.MILLISECONDS);

        LOG.info("Scheduled registration update for {} in {}", registrationId, updateInterval);
    }

    private synchronized void scheduledRegistration() {
        if (registrationId != null && registration != null) {
            registerWithBackend(registration);
        }
    }

    /**
     * Get the registered application ID
     *
     * @return The registered application ID
     */
    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * Set the registered application ID This is usually called internally but can be used for testing
     *
     * @param registrationId The registered application ID
     */
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    /**
     * Deregister application from the backend
     * <p>
     * This method should be called when the application is shutting down to notify the backend that the service is no
     * longer available.
     */
    @PreDestroy
    public synchronized void unregisterFromBackend() {
        if (registrationId != null) {
            try {
                // Deregister with Backend
                restTemplate.delete(registrationServer + "api/registration/service/" + registrationId);
            } catch (Exception e) {
                LOG.error("Failed to deregister from backend", e);
            }

            registrationId = null;
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
