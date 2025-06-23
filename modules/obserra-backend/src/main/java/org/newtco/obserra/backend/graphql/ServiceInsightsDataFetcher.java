package org.newtco.obserra.backend.graphql;

import java.util.List;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import org.newtco.obserra.graphql.client.types.HealthComponentEntry;
import org.newtco.obserra.graphql.client.types.HealthInsight;
import org.newtco.obserra.graphql.client.types.LogGroup;
import org.newtco.obserra.graphql.client.types.Logger;
import org.newtco.obserra.graphql.client.types.LoggersInsight;
import org.newtco.obserra.graphql.client.types.Service;
import org.newtco.obserra.graphql.client.types.ServiceInsights;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/// GraphQL data fetcher component for providing service insight data.
///
/// This Component handles fetching nested data fields related to service insights, including insight summaries, logger
/// levels and groups for services represented in the GraphQL schema. It supports dynamic dispatching for fields within
/// ServiceInsights and filtering loggers and groups by name.
@DgsComponent
public class ServiceInsightsDataFetcher {

    /// Defines custom wiring for the ServiceInsights GraphQL type to use the
    /// [#serviceInsightsFieldByName(DataFetchingEnvironment)] as the default data fetcher.
    ///
    /// @return a RuntimeWiringConfigurer that configures the GraphQL runtime wiring
    @Bean
    public RuntimeWiringConfigurer serviceInsightsWiring() {
        return wiring -> {
            wiring.type("ServiceInsights", type -> type.defaultDataFetcher(this::serviceInsightsFieldByName));
        };
    }

    /// Data fetcher for the "insights" field on the "Service" GraphQL type.
    ///
    /// Retrieves the [ServiceInsights] associated with a [Service].
    ///
    ///
    /// @param dfe the data fetching environment containing the source service object
    ///
    /// @return the ServiceInsights object if present, or null otherwise
    @DgsData(parentType = "Service", field = "insights", trivial = true)
    public ServiceInsights serviceInsights(DgsDataFetchingEnvironment dfe) {
        if (dfe.getSource() instanceof Service service) {
            return service.getInsights();
        }
        return null;
    }

    /// Default data fetcher for fields inside the "ServiceInsights" GraphQL type.
    ///
    /// Resolves dynamically named fields of a ServiceInsights instance by using the Handles utility to map the GraphQL
    /// field to the corresponding Java method or property.
    ///
    ///
    /// @param dfe the data fetching environment for the field being resolved
    ///
    /// @return the value of the resolved field or null if no appropriate handler was found
    private Object serviceInsightsFieldByName(DataFetchingEnvironment dfe) {
        if (dfe.getSource() instanceof ServiceInsights insights) {
            return Handles.forServiceInsightField(dfe.getField())
                .map(handle -> handle.get(insights))
                .orElse(null);
        }

        return null;
    }

    /**
     * Data fetcher for the components field in the HealthInsight type. Transforms the Map<String, HealthComponent> into
     * a list of HealthComponentEntry objects.
     *
     * @param dfe the data-fetching environment
     *
     * @return a list of health component entries
     */
    @DgsData(parentType = "HealthInsight", field = "components", trivial = true)
    public List<HealthComponentEntry> healthComponents(DgsDataFetchingEnvironment dfe, @InputArgument String name) {
        if (dfe.getSource() instanceof HealthInsight health) {
            if (health.getComponents() != null && !health.getComponents().isEmpty()) {
                if (name == null) {
                    return health.getComponents();
                }

                for (var entry : health.getComponents()) {
                    if (name.equalsIgnoreCase(entry.getName())) {
                        return List.of(new HealthComponentEntry(entry.getName(), entry.getComponent()));
                    }
                }
            }

        }
        return List.of();
    }

    /// Data fetcher for the "levels" field on the "LoggersInsight" GraphQL type.
    ///
    /// Returns a list of logger level names for the given LoggersInsight instance.
    ///
    ///
    /// @param dfe the data fetching environment containing the source LoggersInsight object
    ///
    /// @return list of logger levels or null if the source is not LoggersInsight
    @DgsData(parentType = "LoggersInsight", field = "levels", trivial = true)
    public List<String> loggerLevels(DgsDataFetchingEnvironment dfe) {
        if (dfe.getSource() instanceof LoggersInsight loggers) {
            return loggers.getLevels();
        }
        return List.of();
    }

    /// Data fetcher for the "loggers" field on the "LoggersInsight" GraphQL type.
    ///
    /// Returns a list of Logger objects filtered by an optional "name" argument. If the name argument is null, it returns
    /// all loggers.
    ///
    ///
    /// @param dfe  the data fetching environment containing the source LoggersInsight object
    /// @param name an optional input argument to filter loggers by name (case-insensitive)
    ///
    /// @return filtered list of Logger objects or null if the source is not LoggersInsight
    @DgsData(parentType = "LoggersInsight", field = "loggers", trivial = true)
    public List<Logger> loggers(DgsDataFetchingEnvironment dfe, @InputArgument String name) {
        if (dfe.getSource() instanceof LoggersInsight loggers) {
            if (name == null) {
                return loggers.getLoggers();
            }

            for (var logger : loggers.getLoggers()) {
                if (name.equalsIgnoreCase(logger.getName())) {
                    return List.of(logger);
                }
            }
        }

        return List.of();
    }

    /// Data fetcher for the "groups" field on the "LoggersInsight" GraphQL type.
    ///
    /// Returns a list of LogGroup objects filtered by optional "name" argument. If the name argument is null, it
    /// returns all groups.
    ///
    ///
    /// @param dfe  the data fetching environment containing the source LoggersInsight object
    /// @param name an optional input argument to filter groups by name (case-insensitive)
    ///
    /// @return filtered list of LogGroup objects or null if the source is not LoggersInsight
    @DgsData(parentType = "LoggersInsight", field = "groups", trivial = true)
    public List<LogGroup> groups(DgsDataFetchingEnvironment dfe, @InputArgument String name) {
        if (dfe.getSource() instanceof LoggersInsight loggers) {
            if (name == null) {
                return loggers.getGroups();
            }

            for (LogGroup group : loggers.getGroups()) {
                if (name.equalsIgnoreCase(group.getName())) {
                    return List.of(group);
                }
            }
        }

        return List.of();
    }
}