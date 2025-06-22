package org.newtco.obserra.backend.insight;

import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.graphql.client.types.CPUInsight;
import org.newtco.obserra.graphql.client.types.DiskInsight;
import org.newtco.obserra.graphql.client.types.MemoryInsight;
import org.newtco.obserra.graphql.client.types.MetricsInsight;
import org.newtco.obserra.graphql.client.types.ThreadInsight;
import org.newtco.obserra.graphql.client.types.ThreadPoolInsight;
import org.newtco.obserra.graphql.client.types.UptimeInsight;
import org.newtco.obserra.shared.model.ObServiceMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObCPUMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObDiskMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObMemoryMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObThreadMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObThreadPoolMetrics;
import org.newtco.obserra.shared.model.ObServiceMetrics.ObUptimeMetrics;
import org.springframework.stereotype.Service;

import static org.newtco.obserra.backend.util.FormatUtils.formatCount;
import static org.newtco.obserra.backend.util.FormatUtils.formatDiskSpace;
import static org.newtco.obserra.backend.util.FormatUtils.formatDuration;
import static org.newtco.obserra.backend.util.FormatUtils.formatMemory;
import static org.newtco.obserra.backend.util.FormatUtils.formatQuantity;
import static org.newtco.obserra.backend.util.FormatUtils.formatTimestamp;
import static org.newtco.obserra.backend.util.NumberUtils.intValue;
import static org.newtco.obserra.backend.util.NumberUtils.longValue;
import static org.newtco.obserra.backend.util.NumberUtils.percentageOf;

/**
 * Provides insights about the health of a service.
 */
@Service
public class MetricsInsightProvider implements ServiceInsightProvider<MetricsInsight> {


    public MetricsInsightProvider() {
    }

    @Override
    public Class<MetricsInsight> insightType() {
        return MetricsInsight.class;
    }


    @Override
    public MetricsInsight provide(ObService service) {
        if (service.collectorData(insightType()) instanceof ObServiceMetrics metrics) {
            return new MetricsInsight(
                toMemory(metrics),
                toCPU(metrics),
                toThreads(metrics),
                toThreadPool(metrics),
                toUptime(metrics),
                toDisk(metrics)
            );
        }

        return new MetricsInsight();
    }

    private MemoryInsight toMemory(ObServiceMetrics metrics) {
        if (metrics.memory() instanceof ObMemoryMetrics memory) {
            return new MemoryInsight(
                formatMemory(memory.max()),
                formatMemory(memory.maxHeap()),
                formatMemory(memory.maxNonHeap()),
                formatMemory(memory.committed()),
                formatMemory(memory.used()),
                formatMemory(memory.liveDataSize()),
                formatMemory(memory.maxDataSize()),
                percentageOf(memory.used(), memory.maxHeap(), 2)
            );
        }

        return new MemoryInsight();
    }

    private CPUInsight toCPU(ObServiceMetrics metrics) {
        if (metrics.cpu() instanceof ObCPUMetrics cpu) {
            new CPUInsight(
                percentageOf(cpu.processUsage(), 2),
                percentageOf(cpu.systemUsage(), 2),
                intValue(cpu.availableProcessors())
            );
        }

        return new CPUInsight();
    }

    private ThreadInsight toThreads(ObServiceMetrics metrics) {
        if (metrics.threads() instanceof ObThreadMetrics threads) {
            return new ThreadInsight(
                intValue(threads.liveThreads()),
                intValue(threads.daemonThreads()),
                intValue(threads.peakThreads())
            );
        }

        return new ThreadInsight();
    }

    private ThreadPoolInsight toThreadPool(ObServiceMetrics metrics) {
        if (metrics.threadPool() instanceof ObThreadPoolMetrics pool) {
            return new ThreadPoolInsight(
                formatCount(pool.poolSize()),
                formatCount(pool.activeThreads()),
                formatCount(pool.corePoolSize()),
                formatCount(pool.maxPoolSize()),
                formatQuantity(pool.queuedTasks()),
                percentageOf(pool.activeThreads(), pool.maxPoolSize(), 2)
            );
        }
        return new ThreadPoolInsight();
    }

    private UptimeInsight toUptime(ObServiceMetrics metrics) {
        if (metrics.uptime() instanceof ObUptimeMetrics uptime) {
            return new UptimeInsight(
                formatDuration(longValue(uptime.uptime())),
                formatTimestamp(longValue(uptime.startTime()))
            );
        }

        return new UptimeInsight();
    }

    private DiskInsight toDisk(ObServiceMetrics metrics) {
        if (metrics.disk() instanceof ObDiskMetrics disk) {
            return new DiskInsight(
                formatDiskSpace(disk.total()),
                formatDiskSpace(disk.free()),
                percentageOf(disk.used(), disk.total(), 2)
            );
        }
        return new DiskInsight();
    }

}