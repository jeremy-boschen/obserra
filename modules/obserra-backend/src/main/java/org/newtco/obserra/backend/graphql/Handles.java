package org.newtco.obserra.backend.graphql;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import graphql.language.Field;
import org.newtco.obserra.backend.insight.ServiceInsight;
import org.newtco.obserra.graphql.client.types.ServiceInsights;

/**
 * Helper class holding VarHandles of various class types
 */
public class Handles {
    /**
     * Map of ServiceInsights field types and names to respective VarHandles
     */
    private static final Map<String, VarHandle> ServiceInsightsFields = getClassFieldHandles(ServiceInsights.class);


    private static Map<String, VarHandle> getClassFieldHandles(@SuppressWarnings("SameParameterValue") Class<?> clazz) {
        var lookup = MethodHandles.lookup();

        var handles = new HashMap<String, VarHandle>();
        for (var field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                var handle = lookup.unreflectVarHandle(field);
                handles.put(field.getType().getSimpleName(), handle);
                handles.put(field.getName(), handle);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return handles;
    }

    public static Optional<VarHandle> forServiceInsightField(Class<?> type) {
        return Optional.ofNullable(ServiceInsightsFields.get(type.getSimpleName()));
    }

    public static Optional<VarHandle> forServiceInsightField(Field insight) {
        return Optional.ofNullable(ServiceInsightsFields.get(insight.getName()));
    }
}
