package org.newtco.obserra.backend.model.ui;

import java.time.LocalDateTime;
import java.util.Map;

import org.newtco.obserra.backend.model.ObServiceStatus;

public record UiService(
    String id,
    String app,
    String name,
    String version,
    ObServiceStatus status,
    LocalDateTime updated,
    Map<String, ?> insights
) {

}