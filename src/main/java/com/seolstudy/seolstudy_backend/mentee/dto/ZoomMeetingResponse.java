package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeeting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoomMeetingResponse {

    private Long id;
    private String preferredDate;
    private String preferredTime;
    private String status;
    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    public static ZoomMeetingResponse from(ZoomMeeting zoomMeeting) {
        return ZoomMeetingResponse.builder()
                .id(zoomMeeting.getId())
                .preferredDate(zoomMeeting.getPreferredDate().toString())
                .preferredTime(zoomMeeting.getPreferredTime().toString().substring(0, 5))
                .status(zoomMeeting.getStatus().name())
                .createdAt(zoomMeeting.getCreatedAt())
                .confirmedAt(zoomMeeting.getConfirmedAt())
                .build();
    }
}
