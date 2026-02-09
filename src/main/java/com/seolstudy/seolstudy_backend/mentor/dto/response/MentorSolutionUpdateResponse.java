package com.seolstudy.seolstudy_backend.mentor.dto.response;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorSolutionUpdateResponse {

    private Long id;
    private String title;
    private Subject subject;
    private LocalDateTime updatedAt;
}
