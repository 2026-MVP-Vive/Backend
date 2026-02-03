package com.seolstudy.seolstudy_backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 명세서에 정의되어있는 에러 코드를 정의한 클래스입니다.
 * */

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증 실패"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한 없음"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스 없음"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
