package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Notification;
import com.seolstudy.seolstudy_backend.mentee.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // 🚀 필드가 추가되면 생성자도 자동으로 업데이트됩니다.
public class NotificationResponseDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private Long relatedId;
    private boolean isRead;
    private boolean isSent;
    private LocalDateTime createdAt;
    private String studentName;

    public static NotificationResponseDto from(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getRelatedId(),
                notification.isRead(),
                notification.isSent(),
                notification.getCreatedAt(),
                notification.getStudentName()
        );
    }
}