package org.newtco.obserra.shared.model.ui;

/**
 * Base interface for UI component data shared between the backend and frontend.
 */
public interface ServiceInsight<T> {

    /**
     * The type of service insight. E.g. "health", "metrics", etc.
     */
    String type();

    /**
     * The actual insight data.
     */
    T insight();

    static <T> ServiceInsight<T> of(String type, T insight) {
        return new ServiceInsight<T>() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public T insight() {
                return insight;
            }
        };
    }
}
