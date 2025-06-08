package org.newtco.obserra.backend.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import org.newtco.obserra.backend.model.ui.UiService;
import org.newtco.obserra.shared.model.ui.HealthInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight.CPUInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight.DiskInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight.MemoryInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight.ThreadInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight.ThreadPoolInsight;
import org.newtco.obserra.shared.model.ui.MetricsInsight.UptimeInsight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GraphQL data fetcher for service insights.
 * This class provides data fetchers for the nested fields in the Service type.
 */
@DgsComponent
public class ServiceInsightsDataFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInsightsDataFetcher.class);

    /**
     * Data fetcher for the insights field in the Service type.
     * 
     * @param dfe the data-fetching environment
     * @return a map containing the service insights
     */
    @DgsData(parentType = "Service", field = "insights")
    public Map<String, ?> serviceInsights(DgsDataFetchingEnvironment dfe) {
        UiService service = dfe.getSource();
        LOG.debug("Fetching insights for service: {}", service.id());
        return service.insights();
    }

    /**
     * Data fetcher for the health field in the ServiceInsights type.
     * 
     * @param dfe the data-fetching environment
     * @return the health insight
     */
    @DgsData(parentType = "ServiceInsights", field = "health")
    public HealthInsight healthInsight(DgsDataFetchingEnvironment dfe) {
        @SuppressWarnings("unchecked")
        Map<String, ?> insights = (Map<String, ?>) dfe.getSource();
        LOG.debug("Fetching health insight");

        if (insights == null || !insights.containsKey("health")) {
            LOG.debug("No health insight found");
            return null;
        }

        Object healthObj = insights.get("health");
        if (healthObj instanceof HealthInsight) {
            return (HealthInsight) healthObj;
        } else {
            LOG.warn("Health insight is not of expected type: {}", 
                    healthObj != null ? healthObj.getClass().getName() : "null");
            return null;
        }
    }

    /**
     * Data fetcher for the metrics field in the ServiceInsights type.
     * 
     * @param dfe the data-fetching environment
     * @return the metrics insight
     */
    @DgsData(parentType = "ServiceInsights", field = "metrics")
    public MetricsInsight metricsInsight(DgsDataFetchingEnvironment dfe) {
        @SuppressWarnings("unchecked")
        Map<String, ?> insights = (Map<String, ?>) dfe.getSource();
        LOG.debug("Fetching metrics insight");

        if (insights == null || !insights.containsKey("metrics")) {
            LOG.debug("No metrics insight found");
            return null;
        }

        Object metricsObj = insights.get("metrics");
        if (metricsObj instanceof MetricsInsight) {
            return (MetricsInsight) metricsObj;
        } else {
            LOG.warn("Metrics insight is not of expected type: {}", 
                    metricsObj != null ? metricsObj.getClass().getName() : "null");
            return null;
        }
    }

    /**
     * Data fetcher for the components field in the HealthInsight type.
     * Transforms the Map<String, HealthComponent> into a list of HealthComponentEntry objects.
     * 
     * @param dfe the data-fetching environment
     * @return a list of health component entries
     */
    @DgsData(parentType = "HealthInsight", field = "components")
    public List<HealthComponentEntry> healthComponents(DgsDataFetchingEnvironment dfe) {
        HealthInsight healthInsight = dfe.getSource();
        LOG.debug("Fetching health components");

        if (healthInsight == null || healthInsight.components() == null) {
            return List.of();
        }

        List<HealthComponentEntry> entries = new ArrayList<>();
        healthInsight.components().forEach((name, component) -> 
            entries.add(new HealthComponentEntry(name, component))
        );

        return entries;
    }

    /**
     * Data fetcher for the memory field in the MetricsInsight type.
     * 
     * @param dfe the data-fetching environment
     * @return the memory insight
     */
    @DgsData(parentType = "MetricsInsight", field = "memory")
    public MemoryInsight memoryInsight(DgsDataFetchingEnvironment dfe) {
        MetricsInsight metricsInsight = dfe.getSource();
        LOG.debug("Fetching memory insight");
        if (metricsInsight == null) {
            LOG.debug("No metrics insight found");
            return null;
        }
        return metricsInsight.memory();
    }

    /**
     * Data fetcher for the cpu field in the MetricsInsight type.
     * 
     * @param dfe the data-fetching environment
     * @return the CPU insight
     */
    @DgsData(parentType = "MetricsInsight", field = "cpu")
    public CPUInsight cpuInsight(DgsDataFetchingEnvironment dfe) {
        MetricsInsight metricsInsight = dfe.getSource();
        LOG.debug("Fetching CPU insight");
        if (metricsInsight == null) {
            LOG.debug("No metrics insight found");
            return null;
        }
        return metricsInsight.cpu();
    }

    /**
     * Data fetcher for the threads field in the MetricsInsight type.
     * 
     * @param dfe the data-fetching environment
     * @return the thread insight
     */
    @DgsData(parentType = "MetricsInsight", field = "threads")
    public ThreadInsight threadInsight(DgsDataFetchingEnvironment dfe) {
        MetricsInsight metricsInsight = dfe.getSource();
        LOG.debug("Fetching thread insight");
        if (metricsInsight == null) {
            LOG.debug("No metrics insight found");
            return null;
        }
        return metricsInsight.threads();
    }

    /**
     * Data fetcher for the uptime field in the MetricsInsight type.
     * 
     * @param dfe the data-fetching environment
     * @return the uptime insight
     */
    @DgsData(parentType = "MetricsInsight", field = "uptime")
    public UptimeInsight uptimeInsight(DgsDataFetchingEnvironment dfe) {
        MetricsInsight metricsInsight = dfe.getSource();
        LOG.debug("Fetching uptime insight");
        if (metricsInsight == null) {
            LOG.debug("No metrics insight found");
            return null;
        }
        return metricsInsight.uptime();
    }

    /**
     * Data fetcher for the disk field in the MetricsInsight type.
     * 
     * @param dfe the data-fetching environment
     * @return the disk insight
     */
    @DgsData(parentType = "MetricsInsight", field = "disk")
    public DiskInsight diskInsight(DgsDataFetchingEnvironment dfe) {
        MetricsInsight metricsInsight = dfe.getSource();
        LOG.debug("Fetching disk insight");
        if (metricsInsight == null) {
            LOG.debug("No metrics insight found");
            return null;
        }
        return metricsInsight.disk();
    }

    /**
     * Data fetcher for the threadPool field in the MetricsInsight type.
     * 
     * @param dfe the data-fetching environment
     * @return the thread pool insight
     */
    @DgsData(parentType = "MetricsInsight", field = "threadPool")
    public ThreadPoolInsight threadPoolInsight(DgsDataFetchingEnvironment dfe) {
        MetricsInsight metricsInsight = dfe.getSource();
        LOG.debug("Fetching thread pool insight");
        if (metricsInsight == null) {
            LOG.debug("No metrics insight found");
            return null;
        }
        return metricsInsight.threadPool();
    }

    /**
     * Record to represent a health component entry in the GraphQL schema.
     * This is used to transform the Map<String, HealthComponent> into a list of entries.
     */
    public record HealthComponentEntry(
        String name,
        HealthInsight.HealthComponent component
    ) {}
}
