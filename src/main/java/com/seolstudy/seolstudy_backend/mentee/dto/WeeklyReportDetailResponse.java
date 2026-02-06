package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class WeeklyReportDetailResponse {
    private Long id;
    private Integer week;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String summary;
    private List<SubjectReportItem> subjectReports;
    private String overallFeedback;
    private LocalDate createdAt;

    @Getter
    @Builder
    public static class SubjectReportItem {
        private String subject;
        private String subjectName;
        private Integer completionRate;
        private Integer totalStudyTime;
        private String feedback;
    }
}