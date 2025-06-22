package org.newtco.obserra.backend.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing data collected for a service.
 * This class holds data that collectors gather from a service, which is primarily intended for UI display.
 */
public class ServiceData {

    // Health data
    private String healthStatus;
    private JsonNode healthDetails;
    private LocalDateTime healthLastChecked;

    // Metrics data
    private Float memoryUsed;
    private Float memoryMax;
    private Float cpuUsage;
    private Integer errorCount = 0;
    private JsonNode metricData;
    private LocalDateTime metricsLastChecked;

    // Logs data
    private List<ObLog>   recentLogs = new ArrayList<>();
    private LocalDateTime logsLastChecked;

    // Config data
    private List<ConfigProperty> configProperties = new ArrayList<>();
    private LocalDateTime configLastChecked;

    // Getters and Setters

    public String getHealthStatus() {
        return healthStatus;
    }

    public ServiceData setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }

    public JsonNode getHealthDetails() {
        return healthDetails;
    }

    public ServiceData setHealthDetails(JsonNode healthDetails) {
        this.healthDetails = healthDetails;
        return this;
    }

    public LocalDateTime getHealthLastChecked() {
        return healthLastChecked;
    }

    public ServiceData setHealthLastChecked(LocalDateTime healthLastChecked) {
        this.healthLastChecked = healthLastChecked;
        return this;
    }

    public Float getMemoryUsed() {
        return memoryUsed;
    }

    public ServiceData setMemoryUsed(Float memoryUsed) {
        this.memoryUsed = memoryUsed;
        return this;
    }

    public Float getMemoryMax() {
        return memoryMax;
    }

    public ServiceData setMemoryMax(Float memoryMax) {
        this.memoryMax = memoryMax;
        return this;
    }

    public Float getCpuUsage() {
        return cpuUsage;
    }

    public ServiceData setCpuUsage(Float cpuUsage) {
        this.cpuUsage = cpuUsage;
        return this;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public ServiceData setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
        return this;
    }

    public JsonNode getMetricData() {
        return metricData;
    }

    public ServiceData setMetricData(JsonNode metricData) {
        this.metricData = metricData;
        return this;
    }

    public LocalDateTime getMetricsLastChecked() {
        return metricsLastChecked;
    }

    public ServiceData setMetricsLastChecked(LocalDateTime metricsLastChecked) {
        this.metricsLastChecked = metricsLastChecked;
        return this;
    }

    public List<ObLog> getRecentLogs() {
        return recentLogs;
    }

    public ServiceData setRecentLogs(List<ObLog> recentLogs) {
        this.recentLogs = recentLogs;
        return this;
    }

    public ServiceData addLog(ObLog log) {
        if (this.recentLogs == null) {
            this.recentLogs = new ArrayList<>();
        }
        this.recentLogs.add(log);
        return this;
    }

    public LocalDateTime getLogsLastChecked() {
        return logsLastChecked;
    }

    public ServiceData setLogsLastChecked(LocalDateTime logsLastChecked) {
        this.logsLastChecked = logsLastChecked;
        return this;
    }

    public List<ConfigProperty> getConfigProperties() {
        return configProperties;
    }

    public ServiceData setConfigProperties(List<ConfigProperty> configProperties) {
        this.configProperties = configProperties;
        return this;
    }

    public ServiceData addConfigProperty(ConfigProperty property) {
        if (this.configProperties == null) {
            this.configProperties = new ArrayList<>();
        }
        this.configProperties.add(property);
        return this;
    }

    public LocalDateTime getConfigLastChecked() {
        return configLastChecked;
    }

    public ServiceData setConfigLastChecked(LocalDateTime configLastChecked) {
        this.configLastChecked = configLastChecked;
        return this;
    }
}