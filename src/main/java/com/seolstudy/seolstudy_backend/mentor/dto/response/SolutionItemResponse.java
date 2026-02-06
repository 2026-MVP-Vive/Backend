package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class SolutionItemResponse {
    private Long id;
    private String title;
    private String subject;
    private String subjectName;
    private List<MaterialResponse> materials;
    private long linkedTaskCount;
    private LocalDate createdAt;
}

