package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponse {

    // 1. 알림 목록 (배열)
    private List<NotificationResponseDto> notifications;

    // 2. 읽지 않은 알림 총 개수
    private long unreadCount;

    /**
     * 정적 생성 메서드 (필요 시 사용)
     */
    public static NotificationListResponse of(List<NotificationResponseDto> notifications, long unreadCount) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .unreadCount(unreadCount)
                .build();
    }
}
