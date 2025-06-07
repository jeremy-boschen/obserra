package org.newtco.obserra.backend.model.ui;

import java.time.LocalDateTime;
import java.util.Map;

import org.newtco.obserra.backend.model.ServiceStatus;

public record UiService(
    String id,
    String app,
    String name,
    String version,
    ServiceStatus status,
    LocalDateTime updated,
    Map<String, ?> insights
) {

}