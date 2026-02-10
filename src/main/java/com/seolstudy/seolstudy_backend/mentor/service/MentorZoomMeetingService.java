package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentor.dto.response.ZoomMeetingConfirmResponse;
import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeeting;
import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeetingStatus;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.ZoomMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorZoomMeetingService {

    private final ZoomMeetingRepository zoomMeetingRepository;
    private final MentorMenteeRepository mentorMenteeRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public ZoomMeetingConfirmResponse confirmZoomMeeting(Long meetingId) {

        Long mentorId = securityUtil.getCurrentUserId(); // 테스트/실사용 모두 대응

        ZoomMeeting meeting = zoomMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Zoom 미팅을 찾을 수 없습니다."));

        if (meeting.getStatus() != ZoomMeetingStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 Zoom 미팅입니다.");
        }
//
//        // 🔐 멘토-멘티 관계 검증 (중요)
//        mentorMenteeRepository.findByMenteeId(meeting.getMenteeId())
//                .filter(mm -> mm.getMentorId().equals(mentorId))
//                .orElseThrow(() -> new IllegalArgumentException("담당 멘티의 Zoom 미팅이 아닙니다."));

        meeting.confirm();

        User student = userRepository.findById(meeting.getMenteeId())
                .orElseThrow(() -> new IllegalArgumentException("멘티를 찾을 수 없습니다."));

        return new ZoomMeetingConfirmResponse(
                meeting.getId(),
                student.getName(),
                meeting.getPreferredDate(),
                meeting.getPreferredTime(),
                meeting.getStatus(),
                meeting.getConfirmedAt()
        );
    }
}
