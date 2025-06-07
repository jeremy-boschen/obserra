package org.newtco.obserra.backend.core.concurrent;

import java.util.concurrent.StructuredTaskScope;

import org.newtco.obserra.backend.core.functional.RunnableEx;

/// A specialized implementation of [StructuredTaskScope] for handling void-returning tasks that may throw checked
/// exceptions.
///
/// This class simplifies the process of forking tasks that implement the [RunnableEx] interface, allowing tasks to be
/// defined as lambdas or method references that can throw checked exceptions. It ensures that exceptions are properly
/// handled within the structured concurrency framework.
public class RunnableTaskScope extends StructuredTaskScope<Void> {

    public <E extends Exception> Subtask<Void> fork(RunnableEx<E> task) {
        return super.fork(() -> {
            task.run();
            return null;
        });
    }
}
