package com.seolstudy.seolstudy_backend.mentor.dto.response;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.SubmissionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private Subject subject;
    private String subjectName;
    private GoalResponse goal;
    private List<MaterialResponse> materials;
    private Integer studyTime;
    private boolean isMentorConfirmed;
    private SubmissionResponse submission;
    private FeedbackResponse feedback;
}