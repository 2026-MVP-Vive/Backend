package com.seolstudy.seolstudy_backend.mentor.dto.response;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MentorSolutionCreateResponse {

    private Long id;
    private String title;
    private Subject subject;
    private String subjectName;
    private List<MaterialResponse> materials;
    private LocalDateTime createdAt;
}