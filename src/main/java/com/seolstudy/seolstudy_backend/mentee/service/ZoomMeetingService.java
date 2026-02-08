package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeeting;
import com.seolstudy.seolstudy_backend.mentee.dto.ZoomMeetingRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.ZoomMeetingResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.ZoomMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZoomMeetingService {

    private final ZoomMeetingRepository zoomMeetingRepository;

    /**
     * 멘티가 멘토에게 Zoom 미팅을 신청합니다.
     */
    @Transactional
    public ZoomMeetingResponse requestZoomMeeting(Long menteeId, ZoomMeetingRequest request) {
        LocalDate preferredDate = LocalDate.parse(request.getPreferredDate());
        LocalTime preferredTime = LocalTime.parse(request.getPreferredTime());

        ZoomMeeting zoomMeeting = ZoomMeeting.builder()
                .menteeId(menteeId)
                .preferredDate(preferredDate)
                .preferredTime(preferredTime)
                .build();

        ZoomMeeting savedMeeting = zoomMeetingRepository.save(zoomMeeting);

        return ZoomMeetingResponse.from(savedMeeting);
    }
}
