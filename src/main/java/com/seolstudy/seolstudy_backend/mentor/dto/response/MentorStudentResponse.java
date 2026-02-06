package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MentorStudentResponse {

    private List<StudentInfo> students;

    @Getter
    @Builder
    public static class StudentInfo {
        private Long id;
        private String name;
        private String profileImageUrl;
        private TodayTaskSummary todayTaskSummary;
        private LocalDate lastFeedbackDate;
        private boolean hasPendingFeedback;
    }

    @Getter
    @Builder
    public static class TodayTaskSummary {
        private int total;
        private int completed;
        private int pendingConfirmation;
    }
}