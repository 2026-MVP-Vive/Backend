package com.seolstudy.seolstudy_backend.mentee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 1. 특정 유저(멘토/멘티)의 모든 알림을 최신순으로 조회
     */
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 2. 특정 유저의 알림 중 읽지 않은(isRead = false) 것만 최신순으로 조회
     */
    List<Notification> findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 3. 특정 유저의 읽지 않은 알림 개수 카운트 (알림 배지 숫자용)
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 4. [추가] 특정 유저의 모든 알림을 한꺼번에 읽음 처리할 때 사용
     */
    // List<Notification> findAllByUserIdAndIsReadFalse(Long userId);
}