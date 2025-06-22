package org.newtco.obserra.backend.collector.actuator;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.collector.CollectionException;
import org.newtco.obserra.backend.collector.CollectorUtils;
import org.newtco.obserra.backend.collector.config.CollectorConfig;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.MetricsProperties;
import org.newtco.obserra.backend.model.ActuatorEndpoint;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.shared.model.ObServiceMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObCPUMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObDiskMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObMemoryMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObThreadMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObThreadPoolMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObUptimeMetrics;
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
    public String name() {
        return "metrics";
    }

    @Nonnull
    @Override
    public Class<?> type() {
        return ObServiceMetrics.class;
    }

    @Override
    public void collect(ObService service) {
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
    public void collect(ObService service, ActuatorEndpoint endpoint) {
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

    private void collectObserraMetrics(ObService service, ActuatorEndpoint endpoint) {
        var data = webClient.get()
            .uri(endpoint.getHref())
            .retrieve()
            .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
            .body(ObServiceMetrics.class);
        if (data == null) {
            data = new ObServiceMetrics();
        }

        logger.debug("Insights check for service {} returned {}", service.getName(), data);

        service.collectorData(type(), data);
    }

    /**
     * Fallback metrics collector when the obserra endpoint isn't available. Will construct the obserra endpoint by
     * querying each endpoint that makes up the ServiceMetrics record.
     *
     * @param service  the Service object
     * @param endpoint the metrics actuator endpoint
     */

    private void collectMetrics(ObService service, ActuatorEndpoint endpoint) {
        var data = new ObServiceMetrics(
            new ObMemoryMetrics(
                getMetricsEndpointValue(service, endpoint, "jvm.memory.used"),
                getMetricsEndpointValue(service, endpoint, "jvm.memory.committed"),
                getMetricsEndpointValue(service, endpoint, "jvm.memory.max", new QueryParam<>("tag", "area:heap")),
                getMetricsEndpointValue(service, endpoint, "jvm.memory.max", new QueryParam<>("tag", "area:nonheap")),
                getMetricsEndpointValue(service, endpoint, "jvm.gc.live.data.size"),
                getMetricsEndpointValue(service, endpoint, "jvm.gc.max.data.size")
            ),
            new ObCPUMetrics(
                getMetricsEndpointValue(service, endpoint, "process.cpu.usage"),
                getMetricsEndpointValue(service, endpoint, "system.cpu.usage"),
                getMetricsEndpointValue(service, endpoint, "system.cpu.count")
            ),
            new ObThreadMetrics(
                getMetricsEndpointValue(service, endpoint, "jvm.threads.live"),
                getMetricsEndpointValue(service, endpoint, "jvm.threads.daemon"),
                getMetricsEndpointValue(service, endpoint, "jvm.threads.peak")
            ),
            new ObUptimeMetrics(
                getMetricsEndpointValue(service, endpoint, "process.uptime"),
                getMetricsEndpointValue(service, endpoint, "process.start.time")
            ),
            new ObDiskMetrics(
                getMetricsEndpointValue(service, endpoint, "disk.free"),
                getMetricsEndpointValue(service, endpoint, "disk.total")
            ),
            new ObThreadPoolMetrics(
                getMetricsEndpointValue(service, endpoint, "executor.active"),
                getMetricsEndpointValue(service, endpoint, "executor.pool.size"),
                getMetricsEndpointValue(service, endpoint, "executor.pool.core"),
                getMetricsEndpointValue(service, endpoint, "executor.pool.max"),
                getMetricsEndpointValue(service, endpoint, "executor.queued")
            )
        );

        service.collectorData(type(), data);
    }

    private record QueryParam<T>(String name, T value) {

    }

    private Double getMetricsEndpointValue(ObService service, ActuatorEndpoint endpoint, String metric, QueryParam<?>... params) {
        // The endpoint will be the /metrics base endpoint URI. We need to construct the
        // named metric by appending the metric name to the metrics/ URI
        var uri = UriComponentsBuilder.fromUriString(endpoint.getHref())
            .path(metric);
        for (QueryParam<?> param : params) {
            uri.queryParam(param.name, param.value);
        }


        try {
            var descriptor = webClient.get()
                .uri(uri.build().toUri())
                .retrieve()
                .onStatus(CollectorUtils.collectorHttpErrorHandler(service, this))
                .body(MetricDescriptor.class);

            if (descriptor == null || descriptor.getMeasurements().isEmpty()) {
                return null;
            }

            logger.debug("Querying metrics endpoint at {} returned {}", uri, descriptor);

            return descriptor.getMeasurements().getFirst().getValue();
        } catch (CollectionException e) {
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
