package com.seolstudy.seolstudy_backend.mentee.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 각 회원별 역할 저장을 위한 enum 클래스
 * 멘티는 ROLE_MENTEE로 멘토는 ROLE_MENTOR로 각각 DB에 저장됨
 * */

@Getter
@RequiredArgsConstructor
public enum UserRole {
    MENTEE("ROLE_MENTEE", "멘티"),
    MENTOR("ROLE_MENTOR", "멘토");

    private final String key;
    private final String title;
}
