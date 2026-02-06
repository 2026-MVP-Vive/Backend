package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorTaskUpdateResponse {

    private Long id;
    private String title;
    private LocalDate date;
    private LocalDateTime updatedAt;
}

