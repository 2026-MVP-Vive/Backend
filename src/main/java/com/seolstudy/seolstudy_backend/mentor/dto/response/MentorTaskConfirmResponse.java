package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorTaskConfirmResponse {
    private Long id;
    private boolean isMentorConfirmed;
    private LocalDateTime confirmedAt;
}
