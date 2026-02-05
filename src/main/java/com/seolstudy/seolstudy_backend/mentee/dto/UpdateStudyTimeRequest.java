package com.seolstudy.seolstudy_backend.mentee.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateStudyTimeRequest {
    @NotNull(message = "Study time is required")
    @Min(value = 0, message = "Study time cannot be negative")
    private Integer studyTime;
}
