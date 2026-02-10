package com.seolstudy.seolstudy_backend.mentee.domain;

public enum UserRole {
    MENTOR("ROLE_MENTOR"),
    MENTEE("ROLE_MENTEE");

    private final String key;

    UserRole(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}