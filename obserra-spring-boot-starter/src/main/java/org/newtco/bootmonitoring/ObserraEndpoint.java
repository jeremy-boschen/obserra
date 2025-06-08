package org.newtco.bootmonitoring;

import org.newtco.obserra.shared.model.ServiceMetrics.CPUMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.DiskMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.MemoryMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.ThreadMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.ThreadPoolMetrics;
import org.newtco.obserra.shared.model.ServiceMetrics.UptimeMetrics;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;

/**
 * An endpoint for exposing detailed application metrics, providing insight into
 * various resource and performance aspects of the application. This endpoint
 * aggregates metrics such as memory, CPU, threads, uptime, disk, and thread pool usage.
 * <p>
 * The data provided by this endpoint can be used for monitoring, diagnostics, and
 * performance analysis of the application.
 * <p>
 * This endpoint depends on the {@code MetricsEndpoint} to retrieve specific metrics
 * from the application's environment and constructs comprehensive models for each
 * metric category.
 *
 * @apiNote The obserra backend monitor uses this endpoint when available to collect metrics. When not available, it
 * will fall back to doing individual calls to the same metrics/{named} endpoints used here.
 */
@Endpoint(id = "obserra")
public class ObserraEndpoint {

    private final MetricsEndpoint metricsEndpoint;

    public ObserraEndpoint(MetricsEndpoint metricsEndpoint) {
        this.metricsEndpoint = metricsEndpoint;
    }

    @ReadOperation
    public ServiceMetrics getServiceMetrics() {
        return new ServiceMetrics(
            getMemoryMetrics(),
            getCPUMetrics(),
            getThreadMetrics(),
            getUptimeMetrics(),
            getDiskMetrics(),
            getThreadPoolMetrics()
        );
    }

    private MemoryMetrics getMemoryMetrics() {
        return new MemoryMetrics(
            getMetricValue("jvm.memory.used"),
            getMetricValue("jvm.memory.committed"),
            getMetricValue("jvm.memory.max"),
            getMetricValue("jvm.gc.live.data.size"),
            getMetricValue("jvm.gc.max.data.size")
        );
    }

    private CPUMetrics getCPUMetrics() {
        return new CPUMetrics(
            getMetricValue("process.cpu.usage"),
            getMetricValue("system.cpu.usage"),
            getMetricValue("system.cpu.count")
        );
    }

    private ThreadMetrics getThreadMetrics() {
        return new ThreadMetrics(
            getMetricValue("jvm.threads.live"),
            getMetricValue("jvm.threads.daemon"),
            getMetricValue("jvm.threads.peak")
        );
    }

    private UptimeMetrics getUptimeMetrics() {
        return new UptimeMetrics(
            getMetricValue("process.uptime"),
            getMetricValue("process.start.time")
        );
    }

    private DiskMetrics getDiskMetrics() {
        return new DiskMetrics(
            getMetricValue("disk.free"),
            getMetricValue("disk.total")
        );
    }

    private ThreadPoolMetrics getThreadPoolMetrics() {
        return new ThreadPoolMetrics(
            getMetricValue("executor.active"),
            getMetricValue("executor.pool.size"),
            getMetricValue("executor.pool.core"),
            getMetricValue("executor.pool.max"),
            getMetricValue("executor.queued")
        );
    }

    private Double getMetricValue(String metricName) {
        var descriptor = metricsEndpoint.metric(metricName, null);
        if (descriptor == null || descriptor.getMeasurements().isEmpty()) {
            return null;
        }
        return descriptor.getMeasurements().get(0).getValue();
    }
}
