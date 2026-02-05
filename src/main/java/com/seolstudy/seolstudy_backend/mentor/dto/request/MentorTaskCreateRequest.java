package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MentorTaskCreateRequest {

    private String title;
    private LocalDate date;
    private Long goalId;                 // nullable
    private List<Long> materialIds;      // nullable
}
