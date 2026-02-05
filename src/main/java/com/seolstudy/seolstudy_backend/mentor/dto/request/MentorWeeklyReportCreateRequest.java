package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MentorWeeklyReportCreateRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String summary;
    private String overallFeedback;
    private List<SubjectFeedbackRequest> subjectFeedbacks;

    @Getter
    public static class SubjectFeedbackRequest {
        private String subject;   // "KOREAN"
        private String feedback;
    }
}
