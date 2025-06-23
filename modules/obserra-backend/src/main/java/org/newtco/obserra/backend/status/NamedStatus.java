package org.newtco.obserra.backend.status;

import jakarta.annotation.Nonnull;

/**
 * Interface for objects that provide a status string.
 * This allows different types of data to expose their status consistently.
 */
public interface NamedStatus {
    @Nonnull
    String status();
}
