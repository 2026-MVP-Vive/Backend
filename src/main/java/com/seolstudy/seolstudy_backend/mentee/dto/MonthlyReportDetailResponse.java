package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReportDetailResponse {
    private Long id;
    private Integer reportMonth;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String summary;
    private String overallFeedback;
    private LocalDate createdAt;
    private List<SubjectReportDto> subjectReports;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectReportDto {
        private String subject; // Enum name
        private String subjectName; // Display name
        private Integer completionRate;
        private Integer totalStudyTime;
        private String feedback;
    }
}