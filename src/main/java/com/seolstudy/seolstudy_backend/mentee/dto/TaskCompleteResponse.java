package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TaskCompleteResponse {
    private Long id;
    private boolean isMenteeCompleted;
    private LocalDateTime menteeCompletedAt;
}