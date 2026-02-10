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

    /**
     * 현재 로그인한 멘토(사용자)의 알림 목록을 조회합니다.
     * 프론트엔드에서 1분마다 주기적으로 호출(Polling)하는 API입니다.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications() {
        // 1. 현재 로그인한 유저 ID 가져오기
        Long userId = securityUtil.getCurrentUserId();

        // 2. 해당 유저의 알림 목록 조회 (최신순)
        List<NotificationResponseDto> notifications = notificationService.getNotifications(userId);

        return ResponseEntity.ok(notifications);
    }
}
