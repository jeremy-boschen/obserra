package org.newtco.obserra.shared.model;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceRegistration {
    /**
     * Request payload for registering a service with the monitoring dashboard. This class represents the data sent from
     * the Spring Boot application to the backend.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Request {

        @JsonProperty("name")
        private String name;

        @JsonProperty("appId")
        private String appId;

        @JsonProperty("serviceType")
        private String serviceType;

        /**
         * Unique identifier for the service's instance. This should change each time the service is restarted.
         */
        @JsonProperty("serviceId")
        private String serviceId;

        @JsonProperty("version")
        private String version;

        /**
         * The platform/framework running the service being registered
         */
        @JsonProperty("platform")
        private Platform platform;

        @JsonProperty("actuatorUrl")
        private String actuatorUrl;

        @JsonProperty("actuatorPort")
        private int actuatorPort;

        @JsonProperty("checkInterval")
        private Duration checkInterval;

        @JsonProperty("autoRegister")
        private boolean autoRegister;

        // Default constructor for Jackson
        public Request() {
        }

        // Getters and Setters

        public String getName() {
            return name;
        }

        public Request setName(String name) {
            this.name = name;
            return this;
        }

        public String getServiceType() {
            return serviceType;
        }

        public Request setServiceType(String serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        public Platform getPlatform() {
            return platform;
        }

        public Request setPlatform(Platform platform) {
            this.platform = platform;
            return this;
        }

        public String getActuatorUrl() {
            return actuatorUrl;
        }

        public Request setActuatorUrl(String actuatorUrl) {
            this.actuatorUrl = actuatorUrl;
            return this;
        }

        public int getActuatorPort() {
            return actuatorPort;
        }

        public Request setActuatorPort(int actuatorPort) {
            this.actuatorPort = actuatorPort;
            return this;
        }

        public String getAppId() {
            return appId;
        }

        public Request setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public String getServiceId() {
            return serviceId;
        }

        public Request setServiceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public Request setVersion(String version) {
            this.version = version;
            return this;
        }

        public Duration getCheckInterval() {
            return checkInterval;
        }

        public Request setCheckInterval(Duration checkInterval) {
            this.checkInterval = checkInterval;
            return this;
        }

        public boolean isAutoRegister() {
            return autoRegister;
        }

        public Request setAutoRegister(boolean autoRegister) {
            this.autoRegister = autoRegister;
            return this;
        }

        public Request setAutoRegister(Boolean autoRegister) {
            this.autoRegister = autoRegister;
            return this;
        }


        @Override
        public String toString() {
            return "Request{" + "name='" + name + '\'' +
                   ", appId='" + appId + '\'' +
                   ", serviceId='" + serviceId + '\'' +
                   ", version='" + version + '\'' +
                   ", actuatorUrl='" + actuatorUrl + '\'' +
                   ", actuatorPort=" + actuatorPort +
                   ", checkInterval=" + checkInterval +
                   ", autoRegister=" + autoRegister +
                   '}';
        }


        /**
         * The platform/framework running the service being registered. For example, Spring Boot 3.0.0, or Kubernetes
         * 1.2.5
         */
        @JsonInclude(Include.NON_NULL)
        public static class Platform {
            @JsonProperty("name")
            private String name;

            @JsonProperty("version")
            private String version;

            public Platform() {
            }

            public String getName() {
                return name;
            }

            public Platform setName(String name) {
                this.name = name;
                return this;
            }

            public String getVersion() {
                return version;
            }

            public Platform setVersion(String version) {
                this.version = version;
                return this;
            }
        }
    }


    /**
     * Response payload for registering a service with the monitoring dashboard. This class represents the data returned
     * from the backend to the Spring Boot application.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {

        @JsonProperty("id")
        private String registrationId;


        // Default constructor for Jackson
        public Response() {
        }

        // Constructor with all fields
        public Response(String id) {
            this.registrationId = id;
        }

        // Getters and Setters

        public String getRegistrationId() {
            return registrationId;
        }

        public Response setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }
    }
}
