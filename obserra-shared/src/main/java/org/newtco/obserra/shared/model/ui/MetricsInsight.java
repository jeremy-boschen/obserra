
package org.newtco.obserra.shared.model.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.newtco.obserra.shared.model.*;

/**
 * UI model representing comprehensive application insights.
 * Converts ServiceMetrics data into UI-friendly formats.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MetricsInsight(
    MemoryInsight memory,
    CPUInsight cpu,
    ThreadInsight threads,
    UptimeInsight uptime,
    DiskInsight disk,
    ThreadPoolInsight threadPool
) {
    public MetricsInsight() {
        this(null, null, null, null, null, null);
    }

    /**
     * Creates a MetricsInsight instance from ServiceMetrics,
     * converting raw metrics into UI-friendly representations.
     */
    public static MetricsInsight fromServiceMetrics(ServiceMetrics metrics) {
        if (metrics == null) {
            return new MetricsInsight();
        }

        return new MetricsInsight(
            MemoryInsight.fromMemoryMetrics(metrics.memory()),
            CPUInsight.fromCPUMetrics(metrics.cpu()),
            ThreadInsight.fromThreadMetrics(metrics.threads()),
            UptimeInsight.fromUptimeMetrics(metrics.uptime()),
            DiskInsight.fromDiskMetrics(metrics.disk()),
            ThreadPoolInsight.fromThreadPoolMetrics(metrics.threadPool())
        );
    }

    /**
     * UI-friendly representation of memory metrics
     */
    public record MemoryInsight(
        String max,
        String committed,
        String used,
        String liveDataSize,
        String maxDataSize,
        Integer usagePercentage
    ) {
        public MemoryInsight() {
            this(null, null, null, null, null, null);
        }

        public static MemoryInsight fromMemoryMetrics(ServiceMetrics.MemoryMetrics metrics) {
            if (metrics == null) {
                return new MemoryInsight();
            }

            // Calculate memory usage percentage
            Integer usagePercentage = null;
            if (metrics.max() != null && metrics.used() != null && metrics.max() > 0) {
                usagePercentage = (int) ((metrics.used() / metrics.max()) * 100);
            }

            return new MemoryInsight(
                formatMemory(metrics.max()),
                formatMemory(metrics.committed()),
                formatMemory(metrics.used()),
                formatMemory(metrics.liveDataSize()),
                formatMemory(metrics.maxDataSize()),
                usagePercentage
            );
        }

        private static String formatMemory(Double bytes) {
            if (bytes == null) {
                return "N/A";
            }

            // Convert bytes to appropriate unit (KB, MB, GB)
            String[] units = {"B", "KB", "MB", "GB", "TB"};
            int unitIndex = 0;
            double value = bytes;

            while (value > 1024 && unitIndex < units.length - 1) {
                value /= 1024;
                unitIndex++;
            }

            // Format with at most 2 decimal places
            return String.format("%.2f %s", value, units[unitIndex]);
        }
    }

    /**
     * UI-friendly representation of CPU metrics
     */
    public record CPUInsight(
        Integer processUsage,
        Integer systemUsage,
        Integer availableProcessors
    ) {
        public CPUInsight() {
            this(null, null, null);
        }

        public static CPUInsight fromCPUMetrics(ServiceMetrics.CPUMetrics metrics) {
            if (metrics == null) {
                return new CPUInsight();
            }

            // Convert decimal usage (0.0-1.0) to percentage (0-100)
            Integer processUsagePercent = metrics.processUsage() != null ?
                                          (int) (metrics.processUsage() * 100) : null;
            Integer systemUsagePercent = metrics.systemUsage() != null ?
                                         (int) (metrics.systemUsage() * 100) : null;

            Integer availableProcessors = null;
            if (metrics.availableProcessors() != null) {
                availableProcessors = metrics.availableProcessors().intValue();
            }

            return new CPUInsight(
                processUsagePercent,
                systemUsagePercent,
                availableProcessors
            );
        }
    }

    /**
     * UI-friendly representation of thread metrics
     */
    public record ThreadInsight(
        Integer liveThreads,
        Integer daemonThreads,
        Integer peakThreads
    ) {
        public ThreadInsight() {
            this(null, null, null);
        }

        public static ThreadInsight fromThreadMetrics(ServiceMetrics.ThreadMetrics metrics) {
            if (metrics == null) {
                return new ThreadInsight();
            }

            Integer liveThreads = metrics.liveThreads() != null ?
                                  metrics.liveThreads().intValue() : null;
            Integer daemonThreads = metrics.daemonThreads() != null ?
                                    metrics.daemonThreads().intValue() : null;
            Integer peakThreads = metrics.peakThreads() != null ?
                                  metrics.peakThreads().intValue() : null;

            return new ThreadInsight(
                liveThreads,
                daemonThreads,
                peakThreads
            );
        }
    }

    /**
     * UI-friendly representation of uptime metrics
     */
    public record UptimeInsight(
        String uptime,
        String startTime
    ) {
        public UptimeInsight() {
            this(null, null);
        }

        public static UptimeInsight fromUptimeMetrics(ServiceMetrics.UptimeMetrics metrics) {
            if (metrics == null) {
                return new UptimeInsight();
            }

            String formattedUptime = null;
            if (metrics.uptime() != null) {
                formattedUptime = formatDuration(metrics.uptime().longValue() * 1000);
            }

            String formattedStartTime = null;
            if (metrics.startTime() != null) {
                formattedStartTime = formatTimestamp(metrics.startTime().longValue());
            }

            return new UptimeInsight(formattedUptime, formattedStartTime);
        }

        private static String formatDuration(long milliseconds) {
            long seconds = milliseconds / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            hours %= 24;
            minutes %= 60;
            seconds %= 60;

            StringBuilder result = new StringBuilder();
            if (days > 0) {
                result.append(days).append("d ");
            }
            if (hours > 0 || days > 0) {
                result.append(hours).append("h ");
            }
            if (minutes > 0 || hours > 0 || days > 0) {
                result.append(minutes).append("m ");
            }
            result.append(seconds).append("s");

            return result.toString();
        }

        private static String formatTimestamp(long milliseconds) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(milliseconds));
        }
    }

    /**
     * UI-friendly representation of disk metrics
     */
    public record DiskInsight(
        String total,
        String free,
        String usable,
        Integer usagePercentage
    ) {
        public DiskInsight() {
            this(null, null, null, null);
        }

        public static DiskInsight fromDiskMetrics(ServiceMetrics.DiskMetrics metrics) {
            if (metrics == null) {
                return new DiskInsight();
            }

            // Calculate disk usage percentage
            Integer usagePercentage = null;
            if (metrics.total() != null && metrics.free() != null && metrics.total() > 0) {
                double usedSpace = metrics.total() - metrics.free();
                usagePercentage = (int) ((usedSpace / metrics.total()) * 100);
            }

            return new DiskInsight(
                formatDiskSpace(metrics.total()),
                formatDiskSpace(metrics.free()),
                formatDiskSpace(metrics.usable()),
                usagePercentage
            );
        }

        private static String formatDiskSpace(Double bytes) {
            if (bytes == null) {
                return "N/A";
            }

            // Convert bytes to appropriate unit (KB, MB, GB)
            String[] units = {"B", "KB", "MB", "GB", "TB"};
            int unitIndex = 0;
            double value = bytes;

            while (value > 1024 && unitIndex < units.length - 1) {
                value /= 1024;
                unitIndex++;
            }

            // Format with at most 2 decimal places
            return String.format("%.2f %s", value, units[unitIndex]);
        }
    }


    /**
     * UI-friendly representation of thread pool metrics
     */
    public record ThreadPoolInsight(
        String poolSize,
        String activeThreads,
        String corePoolSize,
        String maxPoolSize,
        String queuedTasks,
        Integer usagePercentage
    ) {
        public ThreadPoolInsight() {
            this(null, null, null, null, null, null);
        }

        public static ThreadPoolInsight fromThreadPoolMetrics(ServiceMetrics.ThreadPoolMetrics metrics) {
            if (metrics == null) {
                return new ThreadPoolInsight();
            }

            // Convert raw double values to formatted integers or strings
            String activeThreads = formatThreadCount(metrics.activeThreads());
            String poolSize = formatThreadCount(metrics.poolSize());
            String corePoolSize = formatThreadCount(metrics.corePoolSize());
            String maxPoolSize = formatThreadCount(metrics.maxPoolSize());
            String queuedTasks = formatQueueSize(metrics.queuedTasks());

            // Calculate usage percentage if possible
            Integer usagePercentage = calculateUsagePercentage(
                metrics.activeThreads(),
                metrics.maxPoolSize()
            );

            return new ThreadPoolInsight(
                poolSize,
                activeThreads,
                corePoolSize,
                maxPoolSize,
                queuedTasks,
                usagePercentage
            );
        }

        private static Integer calculateUsagePercentage(Double active, Double max) {
            if (active == null || max == null || max <= 0) {
                return null;
            }
            return (int) ((active / max) * 100);
        }

        private static String formatThreadCount(Double count) {
            if (count == null) {
                return "N/A";
            }
            return Integer.toString(count.intValue());
        }

        private static String formatQueueSize(Double size) {
            if (size == null) {
                return "N/A";
            }

            long queueSize = size.longValue();
            if (queueSize < 1000) {
                return Long.toString(queueSize);
            } else if (queueSize < 1000000) {
                return String.format("%.1fK", queueSize / 1000.0);
            } else {
                return String.format("%.1fM", queueSize / 1000000.0);
            }
        }
    }
}