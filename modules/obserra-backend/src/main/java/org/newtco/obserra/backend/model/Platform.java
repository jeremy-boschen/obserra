package org.newtco.obserra.backend.model;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.shared.model.ServiceRegistration;

public record Platform(@Nonnull String name, Version version) {

    /**
     * Checks if this platform matches the given platform spec. Name comparison is case-insensitive; version matching is
     * delegated.
     */
    public boolean matches(Platform spec) {
        if (!name.equalsIgnoreCase(spec.name)) {
            return false;
        }
        return version.matches(spec.version);
    }

    public Platform ofVersion(String version) {
        return new Platform(name, new Version(version));
    }

    /**
     * Factory method for full spec.
     */
    public static Platform of(String name, String version) {
        return new Platform(name, new Version(version));
    }

    /**
     * Factory for name-only (no version constraint).
     */
    public static Platform of(String name) {
        return new Platform(name, new Version("0"));
    }


    public static Platform from(ServiceRegistration.Request.Platform platform) {
        if (platform == null) {
            throw new IllegalArgumentException("Platform cannot be null");
        }

        return Platform.of(platform.getName(), platform.getVersion());
    }
}
