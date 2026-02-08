package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPlanDto {
    private LocalDate date;
    private String dayOfWeek;
    private int taskCount;
    private int completedCount;
    private boolean hasTask;
}
