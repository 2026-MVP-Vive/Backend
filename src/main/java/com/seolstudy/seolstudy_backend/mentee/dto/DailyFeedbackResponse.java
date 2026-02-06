package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DailyFeedbackResponse {
    private LocalDate date;
    private List<FeedbackItem> feedbacks;
    private String overallComment;
    private String mentorName;

    @Getter
    @Builder
    public static class FeedbackItem {
        private Long id;
        private Long taskId;
        private String taskTitle;
        private Subject subject;
        private String subjectName;
        private boolean isImportant;
        private String summary;
        private String content;
        private LocalDateTime createdAt;
    }
}