package org.newtco.obserra.backend.insight;

import java.util.ArrayList;
import java.util.List;

import org.newtco.obserra.backend.collector.config.properties.CollectionProperties;
import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties;
import org.newtco.obserra.backend.model.HealthData;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.graphql.client.types.HealthComponent;
import org.newtco.obserra.graphql.client.types.HealthComponentEntry;
import org.newtco.obserra.graphql.client.types.HealthInsight;
import org.springframework.stereotype.Service;

/**
 * Provides insights about the health of a service.
 */
@Service
public class HealthInsightProvider implements ServiceInsightProvider<HealthInsight> {

    private final SpringBootProperties.HealthProperties healthProperties;

    public HealthInsightProvider(CollectionProperties properties) {
        this.healthProperties = properties.springBoot().health();
    }

    @Override
    public Class<HealthInsight> insightType() {
        return HealthInsight.class;
    }

    @Override
    public HealthInsight provide(ObService service) {
        var data = service.collectorData(insightType());
        if (data instanceof HealthData health) {
            return new HealthInsight(health.status(),
                                     extractComponents(health));

        }

        return new HealthInsight();
    }

    private List<HealthComponentEntry> extractComponents(HealthData health) {
        if (healthProperties.showComponents()) {
            var components = new ArrayList<HealthComponentEntry>();
            health.components().forEach((name, component) -> {
                components.add(new HealthComponentEntry(name,
                                                        new HealthComponent(component.status(),
                                                                            component.details())));
            });


            return components;
        }

        return List.of();
    }
}
