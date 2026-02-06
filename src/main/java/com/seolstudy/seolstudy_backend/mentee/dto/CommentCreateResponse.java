package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateResponse {

    private Long id;
    private String content;
    private LocalDate date;
    private LocalDateTime createdAt;
}