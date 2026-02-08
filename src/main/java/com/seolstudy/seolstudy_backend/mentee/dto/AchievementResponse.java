package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AchievementResponse {

    private Period period;
    private List<Achievement> achievements;
    private Overall overall;

    @Getter
    @Builder
    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    public static class Achievement {
        private Subject subject;
        private String subjectName;
        private int totalTasks;
        private int completedTasks;
        private int completionRate;
        private int totalStudyTime;
    }

    @Getter
    @Builder
    public static class Overall {
        private int totalTasks;
        private int completedTasks;
        private int completionRate;
        private int totalStudyTime;
    }
}
