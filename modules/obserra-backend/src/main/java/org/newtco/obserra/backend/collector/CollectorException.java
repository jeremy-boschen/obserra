package org.newtco.obserra.backend.collector;

/**
 * Base exception for all collector-related errors.
 */
public class CollectorException extends RuntimeException {
    private final boolean retriable;

    public CollectorException(String message) {
        this(message, null, false);
    }

    public CollectorException(String message, boolean retriable) {
        this(message, null, retriable);
    }

    public CollectorException(String message, Throwable cause) {
        this(message, cause, true);
    }

    public CollectorException(String message, Throwable cause, boolean retriable) {
        super(message, cause);
        this.retriable = retriable;
    }


    /**
     * Indicates whether this exception represents an error that can be retried.
     *
     * @return true if the operation can be retried, false otherwise
     */
    public boolean isRetriable() {
        return retriable;
    }
}