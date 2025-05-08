package org.newtco.obserra.backend.collector.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "obserra.collectors.kubernetes")
public record KubernetesProperties(@DefaultValue("false") boolean enabled) {

}
