package com.seolstudy.seolstudy_backend.mentor.controller; // 패키지 경로는 구조에 맞게 수정하세요

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.NotificationResponseDto;
import com.seolstudy.seolstudy_backend.mentee.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentor")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponseDto>> getMyUnreadNotifications() {
        // 현재 로그인한 멘토 ID 추출
        Long userId = securityUtil.getCurrentUserId();

        // 읽지 않은 알림만 가져오고 isSent 업데이트
        List<NotificationResponseDto> notifications = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok(notifications);
    }
}