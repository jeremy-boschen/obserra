package org.newtco.obserra.backend.model;

import jakarta.annotation.Nonnull;

public interface NamedStatus {
    @Nonnull
    String status();
}
