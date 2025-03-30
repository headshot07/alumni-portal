package com.example.alumniportal.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseStatus {
    SUCCESS,
    FAILURE;

    @JsonCreator
    public static ResponseStatus fromString(String value) {
        for (ResponseStatus status : ResponseStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value for ResponseStatus: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
