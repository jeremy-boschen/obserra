package org.newtco.obserra.backend.collector;

import org.newtco.obserra.backend.model.Service;

/**
 * Interface for collecting data about a service. Various Collector implementations will collect different types of
 * data, such as health checks, metrics, logs, etc.
 */
public interface Collector<T> {

    /**
     * Collect data from a service. The implementation of this method should know what it's collecting and how.
     *
     * @param service The service to collect data from
     *
     * @return A CollectorState containing the collected data and status
     */
    State<T> collect(Service service);

    /**
     * Record representing the state collected by a collector. This is the standard result returned by collectors, which
     * captures the bare minimum for updating service state. The data collector class can take this and update the
     * service, retry collection at a later time and other various operations.
     *
     * @param status
     * @param data
     * @param <T>
     */
    record State<T>(CollectionStatus status, T data) {

        /**
         * Create a new State with a successful status and the provided data
         *
         * @param data The data collected by the collector
         * @param <T>  The type of data collected
         */
        public static <T> State<T> ofSuccess(T data) {
            return State.of(CollectionStatus.SUCCESS, data);
        }

        /**
         * Create a new State with a failure status and no data
         *
         * @param <T> The type of data collected
         */
        public static <T> State<T> ofFailure() {
            return State.of(CollectionStatus.FAILURE, null);
        }

        /**
         * Create a new State with the provided status and data
         *
         * @param status The status of the collection attempt
         * @param data   The data collected by the collector
         * @param <T>    The type of data collected
         */
        public static <T> State<T> of(CollectionStatus status, T data) {
            return new State<>(status, data);
        }

    }

    enum CollectionStatus {
        SUCCESS,
        FAILURE,
    }
}
