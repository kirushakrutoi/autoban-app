package ru.kirill.logistService.models;

import lombok.Getter;

@Getter
public enum Status {
    CREATED("CREATED"),
    STARTED("STARTED"),
    ENDED("ENDED"),
    CANCELLED("CANCELLED"),
    BREAKDOWN("BREAKDOWN"),
    ACCIDENT("ACCIDENT");

    private final String status;

    Status(String s) {
        this.status = s;
    }

    public static Status getStatByString(String status) {
        for (Status env : values()) {
            if (env.getStatus().equals(status)) {
                return env;
            }
        }
        throw new IllegalArgumentException("No enum found with url: [" + status + "]");
    }
}
