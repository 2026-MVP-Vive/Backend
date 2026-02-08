package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.ZoomMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, Long> {
    List<ZoomMeeting> findByMenteeIdOrderByCreatedAtDesc(Long menteeId);
}
