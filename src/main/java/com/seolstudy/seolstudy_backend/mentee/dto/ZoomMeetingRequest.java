package com.seolstudy.seolstudy_backend.mentee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoomMeetingRequest {

    @NotBlank(message = "희망 날짜는 필수입니다")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD이어야 합니다")
    private String preferredDate;

    @NotBlank(message = "희망 시간은 필수입니다")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "시간 형식은 HH:mm이어야 합니다")
    private String preferredTime;
}
