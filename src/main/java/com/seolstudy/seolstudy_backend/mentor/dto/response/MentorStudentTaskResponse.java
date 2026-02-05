package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MentorStudentTaskResponse {
    private Long studentId;
    private String studentName;
    private LocalDate date;
    private List<TaskResponse> tasks;
    private List<Object> comments; // 현재는 빈 배열
}

