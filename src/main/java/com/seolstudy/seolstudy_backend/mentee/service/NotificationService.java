package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.NotificationType;
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

    /**
     * 읽지 않은 알림만 조회하고, 조회된 알림의 전송 상태(isSent)를 true로 변경합니다.
     */
    @Transactional // 🚀 상태 변경이 일어나므로 Transactional 필수!
    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        // 1. 읽지 않은 알림 목록 조회
        List<Notification> unreadNotifications = notificationRepository.findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        // 2. 전달될 알림들의 전송 상태를 true로 변경
        unreadNotifications.forEach(Notification::markAsSent);

        // 3. DTO로 변환하여 반환
        return unreadNotifications.stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }
}
