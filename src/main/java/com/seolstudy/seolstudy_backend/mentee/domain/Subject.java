package com.seolstudy.seolstudy_backend.mentee.domain;

public enum Subject {
    KOREAN("국어"),
    ENGLISH("영어"),
    MATH("수학");

    private final String description;

    Subject(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
