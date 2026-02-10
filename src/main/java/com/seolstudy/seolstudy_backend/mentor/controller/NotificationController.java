package com.seolstudy.seolstudy_backend.mentor.controller;

import com.seolstudy.seolstudy_backend.global.common.ApiResponse; // 프로젝트 공통 응답 클래스 확인!
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.NotificationListResponse; // 새로 만든 리스트용 DTO
import com.seolstudy.seolstudy_backend.mentee.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentor")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    /**
     * 4.15 알림 목록 조회 (unreadOnly 옵션 포함)
     */
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<NotificationListResponse>> getMyNotifications(
            @RequestParam(value = "unreadOnly", defaultValue = "false") boolean unreadOnly) {

        // 1. 현재 로그인한 멘토 ID 추출
        Long userId = securityUtil.getCurrentUserId();
        log.info(String.valueOf(userId));

        // 2. 서비스 호출 (unreadOnly 옵션에 따라 로직 분기)
        NotificationListResponse data = notificationService.getNotifications(userId, unreadOnly);

        // 3. 공통 응답 포맷 { "success": true, "data": { ... } } 에 맞춰 반환
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}