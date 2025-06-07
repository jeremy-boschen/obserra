package org.newtco.obserra.backend.insight;

import org.newtco.obserra.backend.model.Service;
import org.newtco.obserra.shared.model.ui.ServiceInsight;

/**
 * Interface for service insight providers.
 * <p>
 * Insight providers are responsible for providing insights from a service, generally for a specific type of collector.
 * For example, the HealthInsightProvider provides insights about the health of a service based on data collected by the
 * HealthCollector. An insight provider isn't limited to providing insights about a single collector.
 *
 * @param <T> The type of data provided by this provider
 */
public interface ServiceInsightProvider<T> {

    /**
     * The type of data provided by this provider. This should match the type of collector that the provider is
     * associated with.
     */
    String providerType();

    /**
     * Provide insights about service data collected by the collector associated with this provider.
     *
     * @param service The service to provide insights for.
     */
    ServiceInsight<T> provide(Service service);
}
