package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorFeedbackUpdateResponse {
    private Long id;
    private String content;
    private String summary;
    private boolean isImportant;
    private LocalDateTime updatedAt;
}

