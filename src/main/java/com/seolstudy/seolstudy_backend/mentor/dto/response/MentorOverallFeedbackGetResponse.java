package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MentorOverallFeedbackGetResponse {

    private Long id;
    private LocalDate date;
    private String content;
    private boolean hasOverallFeedback;
    private LocalDateTime updatedAt;

    public static MentorOverallFeedbackGetResponse empty(LocalDate date) {
        return new MentorOverallFeedbackGetResponse(
                null,
                date,
                null,
                false,
                null
        );
    }
}
