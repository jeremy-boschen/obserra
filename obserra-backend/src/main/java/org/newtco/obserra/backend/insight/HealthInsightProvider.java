package org.newtco.obserra.backend.insight;

import org.newtco.obserra.backend.collector.config.properties.SpringBootProperties;
import org.newtco.obserra.backend.model.HealthData;
import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.shared.model.ui.HealthInsight;
import org.newtco.obserra.shared.model.ui.ServiceInsight;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides insights about the health of a service.
 */
@Component
public class HealthInsightProvider implements ServiceInsightProvider<HealthInsight> {

    private final SpringBootProperties.HealthProperties healthProperties;

    public HealthInsightProvider(SpringBootProperties config) {
        this.healthProperties = config.health();
    }

    @Override
    public String type() {
        return "health";
    }

    @Override
    public ServiceInsight<HealthInsight> provide(Service service) {
        if (service.collectorData(type()) instanceof HealthData health) {
            return ServiceInsight.of(type(), new HealthInsight(health.status(),
                                                               extractComponents(health)));

        }

        return ServiceInsight.of(type(), null);
    }

    private Map<String, HealthInsight.HealthComponent> extractComponents(HealthData health) {
        if (healthProperties.showComponents()) {
            var components = new LinkedHashMap<String, HealthInsight.HealthComponent>();
            for (var component : health.components().entrySet()) {
                components.put(component.getKey(),
                               new HealthInsight.HealthComponent(component.getValue().status(),
                                                                 component.getValue().details()));
            }

            return components;
        }

        return Map.of();
    }
}
