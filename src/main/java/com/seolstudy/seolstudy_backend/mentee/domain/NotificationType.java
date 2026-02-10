package com.seolstudy.seolstudy_backend.mentee.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // --- 과제(Task) 관련 ---
    TASK_ASSIGNED("새로운 과제가 배정되었습니다."),
    TASK_COMPLETED("멘티가 과제를 완료했습니다."),
    TASK_CONFIRMED("멘토가 과제를 승인했습니다."),
    TASK_UPDATED("과제 내용이 수정되었습니다."),
    TASK_DELETED("과제가 삭제되었습니다."),

    // --- 미팅(Zoom) 관련 ---
    ZOOM_REQUESTED("새로운 미팅 신청이 왔습니다."),
    ZOOM_CONFIRMED("미팅 일정이 확정되었습니다."),
    ZOOM_CANCELLED("미팅이 취소되었습니다."),

    // --- 기타 ---
    SYSTEM_NOTICE("시스템 공지사항이 있습니다.");

    private final String description;
}
