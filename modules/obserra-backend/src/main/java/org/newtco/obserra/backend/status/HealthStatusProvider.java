package org.newtco.obserra.backend.status;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.model.HealthEndpointResponseV3;
import org.springframework.stereotype.Service;

@Service
public class HealthStatusProvider implements ServiceStatusProvider {

    @Nonnull
    @Override
    public Class<?> type() {
        return HealthEndpointResponseV3.class;
    }
}
