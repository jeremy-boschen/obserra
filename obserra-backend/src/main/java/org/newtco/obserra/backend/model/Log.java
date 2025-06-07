package org.newtco.obserra.backend.model;

import java.time.LocalDateTime;

/**
 * Model representing a service log entry.
 * This corresponds to the 'logs' table in the schema.
 */
public class Log {

    private String id;
    private String serviceId;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String level = "INFO";
    private String message;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
