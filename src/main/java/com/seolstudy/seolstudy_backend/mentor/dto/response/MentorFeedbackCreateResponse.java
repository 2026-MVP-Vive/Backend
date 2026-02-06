package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorFeedbackCreateResponse {
    private Long id;
    private Long taskId;
    private String content;
    private String summary;
    private boolean isImportant;
    private LocalDateTime createdAt;
}