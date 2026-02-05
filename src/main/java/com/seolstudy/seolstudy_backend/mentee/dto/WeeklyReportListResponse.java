package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class WeeklyReportListResponse {
    private List<WeeklyReportItem> reports;

    @Getter
    @Builder
    public static class WeeklyReportItem {
        private Long id;
        private Integer week;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isAvailable;
    }
}
