package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MentorStudentSolutionResponse {
    private Long studentId;
    private String studentName;
    private List<SolutionItemResponse> solutions;
}

