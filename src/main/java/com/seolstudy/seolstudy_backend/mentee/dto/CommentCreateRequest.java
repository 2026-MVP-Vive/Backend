package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequest {

    private String content;
    private LocalDate date;
}