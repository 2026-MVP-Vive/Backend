package com.seolstudy.seolstudy_backend.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 실행 성공 후 반환할 응답 형식을 정의한 클래스입니다.
 */

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> successMessage(String message) {
        return new ApiResponse<>(true, null, message);
    }
}
