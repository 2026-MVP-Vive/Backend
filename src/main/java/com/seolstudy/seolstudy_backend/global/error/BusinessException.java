package com.seolstudy.seolstudy_backend.global.error;

import lombok.Getter;

/**
 * 프로젝트 내에서 발생하는 비즈니스 로직 예외를 처리하는 공통 예외 클래스입니다.
 * ErrorCode를 필드로 가져 전역 예외 처리기에서 상태 코드와 메시지를 추출할 수 있게 합니다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    //기본적인 ErrorCode만 받는 생성자
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    //사용자 정의 메시지 추가를 위한 생성자
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}