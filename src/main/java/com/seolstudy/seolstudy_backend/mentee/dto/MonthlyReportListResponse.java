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
public class MonthlyReportListResponse {
    private List<MonthlyReportDto> reports;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyReportDto {
        private Long id;
        private String month; // "2025-01"
        private String title;
        private LocalDate startDate; // Computed: 1st of month
        private LocalDate endDate; // Computed: last of month
        private boolean isAvailable;
    }
}
