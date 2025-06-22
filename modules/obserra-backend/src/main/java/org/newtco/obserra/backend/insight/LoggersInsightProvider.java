package org.newtco.obserra.backend.insight;

import java.util.List;
import java.util.Map.Entry;

import org.newtco.obserra.backend.model.ObLoggerLevel;
import org.newtco.obserra.backend.model.ObLoggers;
import org.newtco.obserra.backend.model.ObLoggers.ServiceLogGroup;
import org.newtco.obserra.backend.model.ObLoggers.ServiceLogger;
import org.newtco.obserra.backend.model.ObService;
import org.newtco.obserra.graphql.client.types.LogGroup;
import org.newtco.obserra.graphql.client.types.Logger;
import org.newtco.obserra.graphql.client.types.LoggersInsight;
import org.newtco.obserra.graphql.client.types.MetricsInsight;
import org.springframework.stereotype.Service;

@Service
public class LoggersInsightProvider implements ServiceInsightProvider<LoggersInsight> {

    public LoggersInsightProvider() {
    }

    @Override
    public Class<LoggersInsight> insightType() {
        return LoggersInsight.class;
    }

    @Override
    public LoggersInsight provide(ObService service) {
        //noinspection DeconstructionCanBeUsed
        if (service.collectorData(insightType()) instanceof ObLoggers data) {
            return new LoggersInsight(data.levels(),
                                      data.loggers().entrySet().stream()
                                          .map(this::toLogger)
                                          .toList(),
                                      data.groups().entrySet().stream()
                                          .map(this::toLogGroup)
                                          .toList());
        }

        return new LoggersInsight(ObLoggerLevel.names(), List.of(), List.of());
    }

    private Logger toLogger(Entry<String, ServiceLogger> entry) {
        var value = entry.getValue();
        return new Logger(entry.getKey(), value.effectiveLevel(), value.configuredLevel());
    }

    private LogGroup toLogGroup(Entry<String, ServiceLogGroup> entry) {
        var value = entry.getValue();
        return new LogGroup(entry.getKey(), value.members());
    }
}
