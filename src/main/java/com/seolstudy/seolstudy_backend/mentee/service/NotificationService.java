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
     * 유저별 알림 목록을 최신순으로 조회합니다.
     */
    public List<NotificationResponseDto> getNotifications(Long userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }
}
