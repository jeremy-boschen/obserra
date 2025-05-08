package org.newtco.obserra.shared.model.ui;

import java.util.List;

public record ServiceInsights(
        String name,
        String status,
        List<ServiceInsight<?>> insights) {

    public ServiceInsights {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
        if (insights == null) {
            throw new IllegalArgumentException("insights cannot be null");
        }
    }
}
