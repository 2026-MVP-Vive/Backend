package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.mentee.domain.*;
import com.seolstudy.seolstudy_backend.mentee.dto.ZoomMeetingRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.ZoomMeetingResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.NotificationRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.ZoomMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZoomMeetingService {

    private final ZoomMeetingRepository zoomMeetingRepository;
    private final MentorMenteeRepository mentorMenteeRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

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
        User user = userRepository.findById(menteeId)
                .orElseThrow(() -> new BusinessException("멘티를 찾을 수 없습니다.", ErrorCode.NOT_FOUND));
        ZoomMeeting savedMeeting = zoomMeetingRepository.save(zoomMeeting);
        MentorMentee mentor_mentee = mentorMenteeRepository.findByMenteeId(menteeId)
                .orElseThrow(() -> new BusinessException("담당 멘토가 존재하지 않습니다.", ErrorCode.NOT_FOUND));
        notificationService.createNotification(
                mentor_mentee.getMentorId(),
                NotificationType.ZOOM_REQUESTED,
                user.getName() + " 학생의 Zoom 요청 접수",
                "담당 멘티 학생의 Zoom 요청이 접수되었습니다.",
                zoomMeeting.getId()
        );

        return ZoomMeetingResponse.from(savedMeeting);
    }

    public List<ZoomMeetingResponse> getZoomMeetings(Long menteeId, String status) {
        List<ZoomMeeting> meetings;

        if (status != null && !status.isEmpty()) {
            ZoomMeetingStatus meetingStatus;
            try {
                meetingStatus = ZoomMeetingStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If invalid status is provided, return empty list or ignored?
                // Spec says "status | String | X" (optional).
                // If provided but invalid, maybe throw error or return empty?
                // I'll throw explicit error or ignore. Let's throw for bad request.
                throw new IllegalArgumentException("Invalid status: " + status);
            }
            meetings = zoomMeetingRepository.findAllByMenteeIdAndStatusOrderByCreatedAtDesc(menteeId, meetingStatus);
        } else {
            meetings = zoomMeetingRepository.findAllByMenteeIdOrderByCreatedAtDesc(menteeId);
        }

        return meetings.stream()
                .map(ZoomMeetingResponse::from)
                .toList();
    }
}
