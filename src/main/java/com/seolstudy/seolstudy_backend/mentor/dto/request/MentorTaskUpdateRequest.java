package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MentorTaskUpdateRequest {

    private String title;      // nullable
    private LocalDate date;    // nullable
    private Long goalId;       // nullable
}
