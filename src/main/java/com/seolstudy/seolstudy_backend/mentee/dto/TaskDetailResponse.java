package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TaskDetailResponse {
    private Long id;
    private String title;
    private LocalDate date;
    private Subject subject;
    private String subjectName;
    private GoalDto goal;
    private Integer studyTime;
    private boolean isUploadRequired;
    private boolean isMentorAssigned;
    private boolean isMentorConfirmed;
    private List<FileDto> materials;
    private SubmissionDto submission;
    private FeedbackDto feedback;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class GoalDto {
        private Long id;
        private String title;
        private Subject subject;
    }

    @Getter
    @Builder
    public static class FileDto {
        private Long id;
        private String fileName;
        private String fileType;
        private Long fileSize;
        private String downloadUrl;
    }

    @Getter
    @Builder
    public static class SubmissionDto {
        private Long id;
        private String imageUrl;
        private LocalDateTime submittedAt;
    }

    @Getter
    @Builder
    public static class FeedbackDto {
        private Long id;
        private String content;
        private String summary;
        private boolean isImportant;
        private LocalDateTime createdAt;
    }
}
