package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyTaskResponse {
    private LocalDate date;
    private List<TaskResponse> tasks;
    private TaskSummary summary;

    @Getter
    @Builder
    public static class TaskSummary {
        private int total;
        private int completed;
        private int totalStudyTime;
    }
}
