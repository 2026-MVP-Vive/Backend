package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;

@Getter
public class MentorFeedbackUpdateRequest {
    private String content;
    private String summary;
    private Boolean isImportant;
}
