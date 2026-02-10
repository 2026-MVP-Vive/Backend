package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotificationItemResponse {
    private Long id;
    private String type;       // NotificationType.name()
    private String title;
    private String message;
    private Long relatedId;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String studentName; // 필요 시 알림 메시지에서 추출하거나 별도 저장

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static NotificationItemResponse from(Notification notification) {
        return NotificationItemResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .relatedId(notification.getRelatedId())
                .isRead(notification.isRead())
                // createdAt 대신 Id 기준으로 정렬하기로 했으니,
                // 만약 시간 필드가 정 없다면 우선 null이나 다른 값을 넣어두세요!
                .build();
    }
}
