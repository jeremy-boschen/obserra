package org.newtco.obserra.shared.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Model representing comprehensive application insights.
 * This record aggregates various metrics about the application's performance and resources.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServiceMetrics(
    MemoryMetrics memory,
    CPUMetrics cpu,
    ThreadMetrics threads,
    UptimeMetrics uptime,
    DiskMetrics disk,
    ThreadPoolMetrics threadPool
) {
    public ServiceMetrics() {
        this(new MemoryMetrics(), new CPUMetrics(), new ThreadMetrics(), new UptimeMetrics(), new DiskMetrics(), new ThreadPoolMetrics());
    }

    /**
     * Model representing thread-related metrics of an application.
     * This record contains information about the number of threads in different states.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record ThreadMetrics(
        Double liveThreads,
        Double daemonThreads,
        Double peakThreads
    ) {
        public ThreadMetrics() {
            this(null,  null, null);
        }
    }

    /**
     * Model representing thread pool metrics of an application.
     * This record contains information about thread pool configuration and usage.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record ThreadPoolMetrics(
        Double activeThreads,
        Double poolSize,
        Double corePoolSize,
        Double maxPoolSize,
        Double queuedTasks
    ) {
        public ThreadPoolMetrics() {
            this(null,null,null,null,null);
        }
    }

    /**
     * Model representing uptime-related metrics of an application.
     * This record contains information about how long the application has been running.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record UptimeMetrics(
        Double uptime,
        Double startTime
    ) {
        public UptimeMetrics() {
            this(null,null);
        }
    }

    /**
     * Model representing disk-related metrics of an application.
     * This record contains information about disk space usage.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record DiskMetrics(
        Double free,
        Double total
    ) {
        public DiskMetrics() {
            this(null,null);
        }

        public Double usable() {
            if (this.free == null || this.total == null) {
                return null;
            }
            return this.total - this.free;
        }
    }

    /**
     * Model representing CPU-related metrics of an application.
     * This record contains information about CPU usage at both process and system levels.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record CPUMetrics(
        Double processUsage,
        Double systemUsage,
        Double availableProcessors
    ) {
        public CPUMetrics() {
            this(null, null, null);
        }
    }

    /**
     * Model representing memory-related metrics of an application.
     * This record contains information about memory usage, allocation, and garbage collection.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record MemoryMetrics(
        Double used,
        Double committed,
        Double max,
        Double liveDataSize,
        Double maxDataSize
    ) {
        public MemoryMetrics() {
            this(null, null, null, null, null);
        }
    }
}