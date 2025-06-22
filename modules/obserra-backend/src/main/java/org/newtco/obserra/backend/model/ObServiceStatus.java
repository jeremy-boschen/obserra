package org.newtco.obserra.backend.model;

/**
 * Enum representing the possible statuses of a service. This corresponds to the ServiceStatusEnum in the schema.
 */
public enum ObServiceStatus {
    UP,
    DOWN,
    PENDING,
    WARNING,
    OUT_OF_SERVICE,
    UNKNOWN;
}