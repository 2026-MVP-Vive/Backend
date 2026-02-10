package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 유저의 알림 목록을 최신순으로 조회
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // 읽지 않은 알림 개수 확인
    long countByUserIdAndIsReadFalse(Long userId);
}
