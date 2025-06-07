package org.newtco.obserra.backend.collector;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Base exception for all collection-related errors.
 */
public class CollectionException extends RuntimeException {
    private final boolean retriable;

    public CollectionException(String message) {
        this(message, null, true);
    }

    public CollectionException(String message, Throwable cause) {
        this(message, cause, true);
    }

    public CollectionException(String message, Throwable cause, boolean retriable) {
        super(message, cause);
        this.retriable = retriable;
    }

    /**
     * Indicates whether this exception represents an error that can be retried.
     * @return true if the operation can be retried, false otherwise
     */
    public boolean isRetriable() {
        return retriable;
    }
}