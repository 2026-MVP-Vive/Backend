package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MentorOverallFeedbackRequest {
    private LocalDate date;
    private String content;
}
