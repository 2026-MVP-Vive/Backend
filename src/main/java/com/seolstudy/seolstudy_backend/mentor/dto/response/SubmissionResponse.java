package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private String imageUrl;
    private String thumbnailUrl;
    private LocalDateTime submittedAt;
}
