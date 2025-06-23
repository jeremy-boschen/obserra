package org.newtco.obserra.backend.graphql;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import graphql.language.Field;
import org.newtco.obserra.graphql.client.types.ServiceInsights;

/**
 * Helper class holding VarHandles for direct field assignment/retrieval by DGS methods
 */
public class Handles {

    /**
     * Map of ServiceInsights field types and names to respective VarHandles
     */
    private static final Map<String, VarHandle> SERVICE_INSIGHTS_FIELDS = getClassFieldHandles(ServiceInsights.class);


    private static Map<String, VarHandle> getClassFieldHandles(@SuppressWarnings("SameParameterValue") Class<?> clazz) {
        try {
            var lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());

            var handles = new HashMap<String, VarHandle>();
            for (var field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    var handle = lookup.unreflectVarHandle(field);
                    handles.put("T:" + field.getType().getName(), handle);
                    handles.put("F:" + field.getName(), handle);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return handles;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<VarHandle> forServiceInsightField(Class<?> insight) {
        return Optional.ofNullable(SERVICE_INSIGHTS_FIELDS.get("T:" + insight.getName()));
    }

    public static Optional<VarHandle> forServiceInsightField(Field insight) {
        return Optional.ofNullable(SERVICE_INSIGHTS_FIELDS.get("F:" + insight.getName()));
    }
}
