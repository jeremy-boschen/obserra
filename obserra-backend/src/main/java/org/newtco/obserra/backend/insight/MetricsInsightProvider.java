package org.newtco.obserra.backend.insight;

import org.newtco.obserra.backend.collector.config.properties.CollectionProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties.HealthProperties.MetricsProperties;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.shared.model.ServiceMetrics;
import org.newtco.obserra.shared.model.ui.MetricsInsight;
import org.newtco.obserra.shared.model.ui.ServiceInsight;
import org.springframework.stereotype.Component;

/**
 * Provides insights about the health of a service.
 */
@Component
public class MetricsInsightProvider implements ServiceInsightProvider<MetricsInsight> {

    private final MetricsProperties metricsProperties;

    public MetricsInsightProvider(CollectionProperties properties) {
        this.metricsProperties = properties.springBoot().metrics();
    }

    @Override
    public String providerType() {
        return "metrics";
    }

    @Override
    public ServiceInsight<MetricsInsight> provide(Service service) {
        var data = service.collectorData(providerType());
        if (data instanceof ServiceMetrics metrics) {
            return ServiceInsight.of(providerType(),
                                     MetricsInsight.fromServiceMetrics(metrics));
        }

        return ServiceInsight.of(providerType(), null);
    }
}