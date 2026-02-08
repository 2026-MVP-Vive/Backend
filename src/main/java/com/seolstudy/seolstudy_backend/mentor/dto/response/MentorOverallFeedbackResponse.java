package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorOverallFeedbackResponse {
    private Long id;
    private LocalDate date;
    private String content;
    private LocalDateTime updatedAt;
}

