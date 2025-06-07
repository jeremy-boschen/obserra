package org.newtco.obserra.backend.core.functional;

@FunctionalInterface
public interface RunnableEx<E extends Exception> {
    void run() throws E;
}
