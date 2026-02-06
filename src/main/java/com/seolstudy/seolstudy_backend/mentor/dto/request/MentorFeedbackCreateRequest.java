package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;

@Getter
public class MentorFeedbackCreateRequest {
    private Long taskId;
    private String content;
    private String summary;
    private Boolean isImportant;
}

