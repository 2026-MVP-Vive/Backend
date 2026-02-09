package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TaskResponse {
    private Long id;
    private String title;
    private Subject subject;
    private String subjectName;
    private Long goalId;
    private String goalTitle;
    private Integer studyTime;
    private boolean isCompleted;
    private boolean isUploadRequired;
    private boolean isMentorAssigned;
    private boolean isMentorConfirmed;
    private boolean isMenteeCompleted;
    private boolean hasSubmission;
    private boolean hasFeedback;
    private int materialCount;

    public static TaskResponse of(Task task, boolean hasSubmission, boolean hasFeedback, int materialCount) {
        boolean isCompleted = hasSubmission
                || (!task.isUploadRequired() && task.getStudyTime() != null && task.getStudyTime() > 0);

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .subject(task.getSubject()) // enum
                .subjectName(task.getSubject() != null ? task.getSubject().getDescription() : null)
                .goalId(task.getSolution() != null ? task.getSolution().getId() : null)
                .goalTitle(task.getSolution() != null ? task.getSolution().getTitle() : null)
                .studyTime(task.getStudyTime())
                .isCompleted(isCompleted)
                .isUploadRequired(task.isUploadRequired())
                .isMentorAssigned(task.isMentorAssigned())
                .isMentorConfirmed(task.isMentorConfirmed())
                .isMenteeCompleted(task.isMenteeCompleted())
                .hasSubmission(hasSubmission)
                .hasFeedback(hasFeedback)
                .materialCount(materialCount)
                .build();
    }
}
