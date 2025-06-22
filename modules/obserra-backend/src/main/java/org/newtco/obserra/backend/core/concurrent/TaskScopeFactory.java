package org.newtco.obserra.backend.core.concurrent;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;

/// Factory for creating task scopes
///
/// - Using a factory for creating StructuredTaskScopes simplifies testing
public interface TaskScopeFactory<T extends StructuredTaskScope<?>> {
    default T create() {
        return create(null, Thread.ofVirtual().factory());
    }

    default T create(String name) {
        return create(name, Thread.ofVirtual().name(name, 1).factory());
    }

    T create(String name, ThreadFactory factory);
}
