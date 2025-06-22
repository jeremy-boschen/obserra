package org.newtco.obserra.backend.graphql;

import java.util.List;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import org.newtco.obserra.graphql.client.types.LogGroup;
import org.newtco.obserra.graphql.client.types.Logger;
import org.newtco.obserra.graphql.client.types.LoggersInsight;
import org.newtco.obserra.graphql.client.types.Service;
import org.newtco.obserra.graphql.client.types.ServiceInsights;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * GraphQL data fetcher for service insights. This class provides data fetchers for the nested fields in the Service
 * type.
 */
@DgsComponent
public class ServiceInsightsDataFetcher {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ServiceInsightsDataFetcher.class);

    @Bean
    public RuntimeWiringConfigurer serviceInsightsWiring() {
        return wiring -> {
            wiring.type("ServiceInsights", type -> type.defaultDataFetcher(this::serviceInsightsFieldByName));
        };
    }


    /**
     * Data fetcher for the insights field in the Service type.
     *
     * @param dfe the data-fetching environment
     *
     * @return a list of ServiceInsight objects
     */
    @DgsData(parentType = "Service", field = "insights", trivial = true)
    public ServiceInsights serviceInsights(DgsDataFetchingEnvironment dfe) {
        if (dfe.getSource() instanceof Service service) {
            return service.getInsights();
        }
        return null;
    }

    /**
     * Data fetcher for ServiceInsights.[field]
     */
    private Object serviceInsightsFieldByName(DataFetchingEnvironment dfe) {
        if (dfe.getSource() instanceof ServiceInsights insights) {
            return Handles.forServiceInsightField(dfe.getField())
                .map(handle -> handle.get(insights))
                .orElse(null);
        }

        return null;
    }


//    /**
//     * Data fetcher for the components field in the HealthInsight type. Transforms the Map<String, HealthComponent> into
//     * a list of HealthComponentEntry objects.
//     *
//     * @param dfe the data-fetching environment
//     *
//     * @return a list of health component entries
//     */
//    @DgsData(parentType = "HealthInsight", field = "components")
//    public List<HealthComponentEntry> healthComponents(DgsDataFetchingEnvironment dfe) {
//        if (dfe.getSource() instanceof HealthInsight health) {
//        if (health.components() == null) {
//            return List.of();
//        }
//
//        var entries = new ArrayList<HealthComponentEntry>();
//        health.components().forEach((name, component) ->
//                                               entries.add(new HealthComponentEntry(name, component))
//        );
//
//        return entries;
//    }

    @DgsData(parentType = "LoggersInsight", field = "levels")
    public List<String> loggerLevels(DgsDataFetchingEnvironment dfe) {
        if (dfe.getSource() instanceof LoggersInsight loggers) {
            return loggers.getLevels();
        }
        return null;
    }

    @DgsData(parentType = "LoggersInsight", field = "loggers")
    public List<Logger> loggers(DgsDataFetchingEnvironment dfe, @InputArgument String name) {
        if (dfe.getSource() instanceof LoggersInsight loggers) {
            if (name == null) {
                return loggers.getLoggers();
            }

            return loggers.getLoggers().stream()
                .filter(logger -> name.equalsIgnoreCase(logger.getName()))
                .toList();
        }

        return null;
    }

    @DgsData(parentType = "LoggersInsight", field = "groups")
    public List<LogGroup> groups(DgsDataFetchingEnvironment dfe, @InputArgument String name) {
        if (dfe.getSource() instanceof LoggersInsight loggers) {
            if (name == null) {
                return loggers.getGroups();
            }

            return loggers.getGroups().stream()
                .filter(group -> name.equalsIgnoreCase(group.getName()))
                .toList();
        }

        return null;
    }
}
