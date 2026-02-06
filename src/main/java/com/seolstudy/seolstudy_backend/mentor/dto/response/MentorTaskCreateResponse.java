package com.seolstudy.seolstudy_backend.mentor.dto.response;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MentorTaskCreateResponse {

    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private Subject subject;
    private GoalResponse goal;
    private List<MaterialResponse> materials;
}