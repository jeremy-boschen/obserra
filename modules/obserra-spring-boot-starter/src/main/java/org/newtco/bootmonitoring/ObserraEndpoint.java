package org.newtco.bootmonitoring;

import java.util.List;

import org.newtco.obserra.shared.model.ObServiceMetrics.ObCPUMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObDiskMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObMemoryMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObThreadMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObThreadPoolMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObUptimeMetrics;
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
    public ObServiceMetrics getServiceMetrics() {
        return new ObServiceMetrics(
            getMemoryMetrics(),
            getCPUMetrics(),
            getThreadMetrics(),
            getUptimeMetrics(),
            getDiskMetrics(),
            getThreadPoolMetrics()
        );
    }

    private ObMemoryMetrics getMemoryMetrics() {
        return new ObMemoryMetrics(
            getMetricValue("jvm.memory.used"),
            getMetricValue("jvm.memory.committed"),
            getMetricValue("jvm.memory.max", "area:heap"),
            getMetricValue("jvm.memory.max", "area:nonheap"),
            getMetricValue("jvm.gc.live.data.size"),
            getMetricValue("jvm.gc.max.data.size")
        );
    }

    private ObCPUMetrics getCPUMetrics() {
        return new ObCPUMetrics(
            getMetricValue("process.cpu.usage"),
            getMetricValue("system.cpu.usage"),
            getMetricValue("system.cpu.count")
        );
    }

    private ObThreadMetrics getThreadMetrics() {
        return new ObThreadMetrics(
            getMetricValue("jvm.threads.live"),
            getMetricValue("jvm.threads.daemon"),
            getMetricValue("jvm.threads.peak")
        );
    }

    private ObUptimeMetrics getUptimeMetrics() {
        return new ObUptimeMetrics(
            getMetricValue("process.uptime"),
            getMetricValue("process.start.time")
        );
    }

    private ObDiskMetrics getDiskMetrics() {
        return new ObDiskMetrics(
            getMetricValue("disk.free"),
            getMetricValue("disk.total")
        );
    }

    private ObThreadPoolMetrics getThreadPoolMetrics() {
        return new ObThreadPoolMetrics(
            getMetricValue("executor.active"),
            getMetricValue("executor.pool.size"),
            getMetricValue("executor.pool.core"),
            getMetricValue("executor.pool.max"),
            getMetricValue("executor.queued")
        );
    }

    private Double getMetricValue(String metricName, String... tags) {
        var tag = tags.length > 0 ? List.of(tags) : null;
        var descriptor = metricsEndpoint.metric(metricName, tag);
        if (descriptor == null || descriptor.getMeasurements().isEmpty()) {
            return null;
        }
        return descriptor.getMeasurements().get(0).getValue();
    }
}
