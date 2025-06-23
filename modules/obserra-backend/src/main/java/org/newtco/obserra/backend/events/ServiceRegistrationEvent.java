package org.newtco.obserra.backend.events;

public class ServiceRegistrationEvent {
    private final String serviceId;

    public ServiceRegistrationEvent(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }
}
