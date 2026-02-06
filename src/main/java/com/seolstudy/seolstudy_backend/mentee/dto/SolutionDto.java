package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionDto {
    private Long id;
    private String title;
    private Subject subject;
    private String subjectName;
    private List<SolutionMaterialDto> materials;

    public static String getSubjectName(Subject subject) {
        return switch (subject) {
            case KOREAN -> "국어";
            case ENGLISH -> "영어";
            case MATH -> "수학";
        };
    }
}
