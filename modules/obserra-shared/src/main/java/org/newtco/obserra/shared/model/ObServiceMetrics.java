package org.newtco.obserra.shared.model;

import java.util.Objects;

/**
 * Model representing comprehensive application insights. This record aggregates various metrics about the application's
 * performance and resources.
 */
public record ObServiceMetrics(
    ObMemoryMetrics memory,
    ObCPUMetrics cpu,
    ObThreadMetrics threads,
    ObUptimeMetrics uptime,
    ObDiskMetrics disk,
    ObThreadPoolMetrics threadPool
) {
    public static final String TYPE = "metrics";

    public ObServiceMetrics() {
        this(new ObMemoryMetrics(), new ObCPUMetrics(), new ObThreadMetrics(), new ObUptimeMetrics(), new ObDiskMetrics(), new ObThreadPoolMetrics());
    }

    /**
     * Model representing thread-related metrics of an application. This record contains information about the number of
     * threads in different states.
     */
    public record ObThreadMetrics(
        Double liveThreads,
        Double daemonThreads,
        Double peakThreads
    ) {
        public ObThreadMetrics() {
            this(null, null, null);
        }
    }

    /**
     * Model representing thread pool metrics of an application. This record contains information about thread pool
     * configuration and usage.
     */
    public record ObThreadPoolMetrics(
        Double activeThreads,
        Double poolSize,
        Double corePoolSize,
        Double maxPoolSize,
        Double queuedTasks
    ) {
        public ObThreadPoolMetrics() {
            this(null, null, null, null, null);
        }
    }

    /**
     * Model representing uptime-related metrics of an application. This record contains information about how long the
     * application has been running.
     */
    public record ObUptimeMetrics(
        Double uptime,
        Double startTime
    ) {
        public ObUptimeMetrics() {
            this(null, null);
        }
    }

    /**
     * Model representing disk-related metrics of an application. This record contains information about disk space
     * usage.
     */
    public record ObDiskMetrics(
        Double free,
        Double total
    ) {
        public ObDiskMetrics() {
            this(null, null);
        }

        public Double used() {
            if (this.free == null || this.total == null) {
                return null;
            }
            return  this.total - this.free;
        }
    }

    /**
     * Model representing CPU-related metrics of an application. This record contains information about CPU usage at
     * both process and system levels.
     */
    public record ObCPUMetrics(
        Double processUsage,
        Double systemUsage,
        Double availableProcessors
    ) {
        public ObCPUMetrics() {
            this(null, null, null);
        }
    }

    /**
     * Model representing memory-related metrics of an application. This record contains information about memory usage,
     * allocation, and garbage collection.
     */
    public record ObMemoryMetrics(
        Double used,
        Double committed,
        Double maxHeap,
        Double maxNonHeap,
        Double liveDataSize,
        Double maxDataSize
    ) {
        public ObMemoryMetrics() {
            this(null, null, null, null, null, null);
        }

        public Double max() {
            return null != maxHeap && null != maxNonHeap
                   ? maxHeap + maxNonHeap
                   : 0f;
        }
    }
}