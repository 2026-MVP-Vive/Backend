package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.NotificationType;
import com.seolstudy.seolstudy_backend.mentee.dto.NotificationListResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.NotificationResponseDto;
import com.seolstudy.seolstudy_backend.mentee.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seolstudy.seolstudy_backend.mentee.domain.Notification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림을 DB에 저장합니다. (FCM 전송 제외)
     */
    @Transactional
    public void createNotification(Long userId, NotificationType type, String title, String body, Long relatedId) {
        // 1. DB에 알림 저장
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .body(body)
                .relatedId(relatedId)
                .build();

        notificationRepository.save(notification);
    }

    // NotificationService.java 내부
    @Transactional
    public NotificationListResponse getNotifications(Long userId, boolean unreadOnly) {
        // 1. 조회 (로그에 찍힌 그 쿼리가 나갑니다)
        List<Notification> notifications = unreadOnly
                ? notificationRepository.findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                : notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // 🚀 [핵심] 리스트가 증발하기 전에 DTO로 먼저 복사해두기
        List<NotificationResponseDto> responseDtos = notifications.stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());

        // 2. 그 다음 DB 상태 변경 (is_sent = true)
        notifications.forEach(Notification::markAsSent);

        // 3. 카운트 조회
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        // 4. 미리 복사해둔 responseDtos를 반환!
        return NotificationListResponse.builder()
                .notifications(responseDtos)
                .unreadCount(unreadCount)
                .build();
    }
}
