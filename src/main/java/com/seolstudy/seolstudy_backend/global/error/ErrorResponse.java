package com.seolstudy.seolstudy_backend.global.error;

import lombok.Builder;

@Builder
public record ErrorResponse(
        boolean success,
        String data,
        String message,
        String errorCode
) {
    // of 메서드 같은 정적 팩토리 메서드도 안에 넣을 수 있어
    public static ErrorResponse of(ErrorCode code) {
        return ErrorResponse.builder()
                .success(false)
                .data(null)
                .message(code.getMessage())
                .errorCode(code.getCode())
                .build();
    }
}