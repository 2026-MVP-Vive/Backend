package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Submission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionResponse {
    private Long id;
    private Long taskId;
    private String imageUrl;
    private LocalDateTime submittedAt;

    public static SubmissionResponse of(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .taskId(submission.getTaskId())
                .imageUrl(submission.getFileId() != null ? "/api/v1/files/" + submission.getFileId() : null)
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
