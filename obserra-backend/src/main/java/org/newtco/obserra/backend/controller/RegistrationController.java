package org.newtco.obserra.backend.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.newtco.obserra.backend.collector.actuator.DiscoveryService;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.backend.model.ServiceStatus;
import org.newtco.obserra.backend.storage.Storage;
import org.newtco.obserra.shared.model.ErrorResponse;
import org.newtco.obserra.shared.model.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for service registration. This controller provides endpoints for services to register themselves with the
 * server.
 */
@RestController
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final Storage          storage;
    private final DiscoveryService discoveryService;

    @Autowired
    public RegistrationController(Storage storage, DiscoveryService discoveryService) {
        this.storage          = storage;
        this.discoveryService = discoveryService;
    }

    /**
     * Register a service. This endpoint allows services to register themselves with the server.
     *
     * @param registration the service registration object
     *
     * @return the registered service
     */
    @RequestMapping(
        path = "/api/registration/service",
        method = RequestMethod.POST,
        consumes = "application/json",
        produces = "application/json"
    )
    public ResponseEntity<?> registerService(HttpServletRequest serverRequest, @RequestBody ServiceRegistration.Request registration) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("appId: {} - Received service registration request: {}", registration.getAppId(), registration);
            } else {
                logger.debug("appId: {} - Received service registration request", registration.getAppId());
            }

            // Validate required fields
            if (registration.getName() == null || registration.getActuatorUrl() == null) {
                return ResponseEntity.badRequest()
                    .body("Missing required fields: name, actuatorUrl");
            }

            var actuatorUrl = buildRegistrationActuatorUrl(registration, serverRequest);
            logger.debug("appId:{} - Using callback actuator URL '{}'", registration.getAppId(), actuatorUrl);

            var service = new Service()
                .setId(registration.getServiceId())
                .setName(registration.getName())
                .setAppId(registration.getAppId())
                .setVersion(registration.getVersion())
                .setActuatorUrl(actuatorUrl)
                .setAutoRegister(registration.isAutoRegister())
                .setCheckInterval(registration.getCheckInterval());

            // Discover available actuator endpoints
            try {
                var endpoints = discoveryService.discoverServiceEndpoints(service);
                if (!endpoints.isEmpty()) {
                    // Update the service with discovered endpoints
                    service.setActuatorEndpoints(endpoints);
                    logger.debug("Discovered {} actuator endpoints for service {}", endpoints.size(), service.getName());
                } else {
                    logger.warn("No actuator endpoints discovered for service {}", service.getName());
                }
            } catch (Exception e) {
                logger.error("Error discovering actuator endpoints for service {}", service.getName(), e);
            }

            service = storage.updateServiceByAppId(registration.getAppId(), service);

//            // Unregister the existing service if it exists
//            var existing = storage.getServiceByAppId(registration.getAppId());
//            if (existing.isEmpty()) {
//                service = storage.createService(service);
//                logger.info("Created service registration: {} ({}/{})", service.getName(), service.getId(), service.getAppId());
//            } else {
//                service = storage.updateService(existing.get().getId(), service);
//                logger.debug("Updated service registration: {} ({}/{})", service.getName(), service.getId(), service.getAppId());
//            }

            // Return the registered service
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ServiceRegistration.Response(
                    service.getId()
                ));
        } catch (Exception e) {
            logger.error("Error registering service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to register service"));
        }
    }



    /**
     * Deregister a service by appId. This endpoint allows services to deregister themselves when they shut down.
     *
     * @param registrationId Registration ID provided by the response to registration
     *
     * @return success or error response
     */
    @RequestMapping(
        path = "/api/registration/service/{registrationId}",
        method = RequestMethod.DELETE,
        consumes = "application/json",
        produces = "application/json"
    )
    public ResponseEntity<?> deregisterService(@PathVariable String registrationId) {
        try {
            logger.info("Received deregistration request for appId: {}", registrationId);

            Optional<Service> serviceOpt = storage.getServiceByAppId(registrationId);
            if (serviceOpt.isPresent()) {
                Service service = serviceOpt.get();
                service.setStatus(ServiceStatus.DOWN);
                storage.updateService(service.getId(), service);
                logger.info("Service deregistered: {} ({})", service.getName(), registrationId);
                return ResponseEntity.ok().build();
            } else {
                logger.warn("Service not found for deregistration: {}", registrationId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Service not found"));
            }
        } catch (Exception e) {
            logger.error("Error deregistering service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to deregister service"));
        }
    }

    private String buildRegistrationActuatorUrl(ServiceRegistration.Request registration, HttpServletRequest serverRequest) {
        // If the client passed a full URL, use it
        var actuatorUrl = registration.getActuatorUrl();
        if (actuatorUrl.endsWith("/")) {
            actuatorUrl = actuatorUrl.substring(0, actuatorUrl.length() - 1);
        }

        if (actuatorUrl.startsWith("http:") || actuatorUrl.startsWith("https:")) {
            return actuatorUrl;
        }

        // Determine the calling client's hostname/port for constructing the callback URL
        var clientHost = serverRequest.getHeader("X-Forwarded-For");
        if (clientHost != null && !clientHost.isBlank()) {
            logger.debug("appId:{} - Using X-Forwarded-For header for client host {}", registration.getAppId(), clientHost);
        } else {
            clientHost = serverRequest.getRemoteHost();
            if (clientHost != null && !clientHost.isBlank()) {
                logger.debug("appId:{} - Using remote host for client host {}", registration.getAppId(), clientHost);
            } else {
                clientHost = serverRequest.getRemoteAddr();
                logger.debug("appId:{} - Using remote address for client host {}", registration.getAppId(), clientHost);
            }
        }

        var clientPort = "";
        if (registration.getActuatorPort() > 0) {
            clientPort = String.valueOf(registration.getActuatorPort());
        } else {
            clientPort = serverRequest.getHeader("X-Forwarded-Port");
            if (clientPort != null && !clientPort.isBlank()) {
                logger.debug("appId:{} - Using X-Forwarded-Port header for client port {}", registration.getAppId(), clientPort);
            } else {
                clientPort = String.valueOf(serverRequest.getRemotePort());
                logger.debug("appId:{} - Using remote port for client port {}", registration.getAppId(), clientPort);
            }
        }

        var scheme = clientPort.endsWith("443") ? "https" : "http";

        return scheme + "://" + clientHost + ":" + clientPort + (actuatorUrl.startsWith("/") ? "" : "/") + actuatorUrl;
    }
}
