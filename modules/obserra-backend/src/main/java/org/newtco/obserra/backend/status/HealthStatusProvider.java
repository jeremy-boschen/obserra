package org.newtco.obserra.backend.status;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.insight.ServiceStatusProvider;
import org.newtco.obserra.backend.model.HealthData;
import org.springframework.stereotype.Service;

@Service
public class HealthStatusProvider implements ServiceStatusProvider {

    @Nonnull
    @Override
    public String type() {
        return HealthData.TYPE;
    }
}
