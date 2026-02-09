package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeeting;
import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, Long> {
    List<ZoomMeeting> findAllByMenteeIdOrderByCreatedAtDesc(Long menteeId);

    List<ZoomMeeting> findAllByMenteeIdAndStatusOrderByCreatedAtDesc(Long menteeId, ZoomMeetingStatus status);
}
