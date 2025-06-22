package org.newtco.obserra.backend.insight;

import org.newtco.obserra.backend.model.ObService;

/**
 * Interface for service insight providers.
 * <p>
 * Insight providers are responsible for providing insights from a service, generally for a specific type of collector.
 * For example, the HealthInsightProvider provides insights about the health of a service based on data collected by the
 * HealthCollector. An insight provider isn't limited to providing insights about a single collector, though, and can
 * merge multiple collector's data into a single insight.
 */
public interface ServiceInsightProvider<T> {

    default String name() {
        return insightType().getSimpleName().replace("Insight", "").toLowerCase();
    }

    /**
     * The type of data provided by this provider. This should match the type of collector that the provider is
     * associated with.
     */
    Class<T> insightType();

    /**
     * Provide insights about service data collected by the collector associated with this provider.
     *
     * @param service The service to provide insights for.
     */
    T provide(ObService service);
}
