package com.seolstudy.seolstudy_backend.mentor.dto.response;

import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ZoomMeetingConfirmResponse {

    private Long id;
    private String studentName;
    private LocalDate preferredDate;
    private LocalTime preferredTime;
    private ZoomMeetingStatus status;
    private LocalDateTime confirmedAt;
}
