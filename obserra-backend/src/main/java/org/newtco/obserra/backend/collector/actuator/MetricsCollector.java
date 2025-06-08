package org.newtco.obserra.backend.collector.actuator;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.collector.CollectionException;
import org.newtco.obserra.backend.collector.CollectorUtils;
import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.MetricsProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.shared.model.ServiceMetrics.CPUMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.DiskMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.MemoryMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.ThreadMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.ThreadPoolMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.UptimeMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class MetricsCollector implements ActuatorCollector<MetricsProperties> {
    private static final Logger logger = LoggerFactory.getLogger(MetricsCollector.class);

    private final RestClient        webClient;
    private final MetricsProperties metricsProperties;

    @Autowired
    public MetricsCollector(
        CollectorConfig config
    ) {
        this.metricsProperties = config.properties().collectors().springBoot().metrics();
        this.webClient         = config.webClient();
    }

    @Nonnull
    @Override
    public String type() {
        return "metrics";
    }

    @Override
    public void collect(Service service) {
        // Try to use the obserra endpoint if it's available
        var actuatorEndpoint = service.findActuatorEndpoint("obserra");
        if (actuatorEndpoint.isEmpty()) {
            // Fallback to the metrics endpoint
            actuatorEndpoint = service.findActuatorEndpoint("metrics");
        }

        actuatorEndpoint.ifPresent(endpoint -> collect(service, endpoint));
    }

    @Override
    public boolean canCollect(ActuatorEndpoint endpoint) {
        return endpoint.getType().equals("metrics") || endpoint.getType().equals("obserra");
    }

    @Override
    public void collect(Service service, ActuatorEndpoint endpoint) {
        logger.debug("Checking metrics for service: {} ({})", service.getName(), service.getId());

        switch (endpoint.getType()) {
            case "obserra":
                collectObserraMetrics(service, endpoint);
                break;
            case "metrics":
                collectMetrics(service, endpoint);
                break;
            default:
                logger.warn("Unknown actuator endpoint type: {}", endpoint.getType());
                break;
        }
    }

    private void collectObserraMetrics(Service service, ActuatorEndpoint endpoint) {
        var data = webClient.get()
            .uri(endpoint.getHref())
            .retrieve()
            .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
            .body(ServiceMetrics.class);
        if (data == null) {
            data = new ServiceMetrics();
        }

        logger.debug("Insights check for service {} returned {}", service.getName(), data);

        service.collectorData(type(), data);
    }

    /**
     * Fallback metrics collector when the obserra endpoint isn't available. Will construct
     * the obserra endpoint by querying each endpoint that makes up the ServiceMetrics
     * record.
     * @param service the Service object
     * @param endpoint the metrics actuator endpoint
     */

    private void collectMetrics(Service service, ActuatorEndpoint endpoint) {
        var data = new ServiceMetrics(
            new MemoryMetrics(
                getMetricsEndpointValue(service, endpoint, "jvm.memory.used"),
                getMetricsEndpointValue(service, endpoint, "jvm.memory.committed"),
                getMetricsEndpointValue(service, endpoint, "jvm.memory.max"),
                getMetricsEndpointValue(service, endpoint, "jvm.gc.live.data.size"),
                getMetricsEndpointValue(service, endpoint, "jvm.gc.max.data.size")
            ),
            new CPUMetrics(
                getMetricsEndpointValue(service, endpoint, "process.cpu.usage"),
                getMetricsEndpointValue(service, endpoint, "system.cpu.usage"),
                getMetricsEndpointValue(service, endpoint, "system.cpu.count")
            ),
            new ThreadMetrics(
                getMetricsEndpointValue(service, endpoint, "jvm.threads.live"),
                getMetricsEndpointValue(service, endpoint, "jvm.threads.daemon"),
                getMetricsEndpointValue(service, endpoint, "jvm.threads.peak")
            ),
            new UptimeMetrics(
                getMetricsEndpointValue(service, endpoint, "process.uptime"),
                getMetricsEndpointValue(service, endpoint, "process.start.time")
            ),
            new DiskMetrics(
                getMetricsEndpointValue(service, endpoint, "disk.free"),
                getMetricsEndpointValue(service, endpoint, "disk.total")
            ),
            new ThreadPoolMetrics(
                getMetricsEndpointValue(service, endpoint, "executor.active"),
                getMetricsEndpointValue(service, endpoint, "executor.pool.size"),
                getMetricsEndpointValue(service, endpoint, "executor.pool.core"),
                getMetricsEndpointValue(service, endpoint, "executor.pool.max"),
                getMetricsEndpointValue(service, endpoint, "executor.queued")
            )
        );

        service.collectorData(type(), data);
    }

    private Double getMetricsEndpointValue(Service service, ActuatorEndpoint endpoint, String metric) {
        // The endpoint will be the /metrics base endpoint URI. We need to construct the
        // named metric by appending the metric name to the metrics/ URI
        var uri = UriComponentsBuilder.fromUriString(endpoint.getHref())
            .path(metric)
            .build().toUri();

        try {
            var descriptor = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
                .body(MetricDescriptor.class);

            if (descriptor == null || descriptor.getMeasurements().isEmpty()) {
                return null;
            }

            logger.debug("Querying metrics endpoint at {} returned {}", uri, descriptor);

            return descriptor.getMeasurements().getFirst().getValue();
        }
        catch (CollectionException e) {
            logger.warn("Failed to collect metric {} for service {} from {}: {}",
                        metric, service.getName(), uri, e.getMessage(), e);
            return null;

        }
    }


    @Nonnull
    @Override
    public MetricsProperties properties() {
        return metricsProperties;
    }
}
