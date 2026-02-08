package com.seolstudy.seolstudy_backend.global.error;

import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * 전역 예외 처리 클래스입니다.
 * 서버 내 발생하는 모든 예외를 공통된 에러 응답 형식(ErrorResponse)으로 변환합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 에러 응답 생성 메서드
    private ResponseEntity<ErrorResponse> makeErrorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .success(false)
                        .data(null)
                        .message(message != null ? message : errorCode.getMessage())
                        .errorCode(errorCode.getCode())
                        .build());
    }

    /**
     * 1. @Valid 유효성 검사 실패 시 발생 (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException: {}", e.getMessage());
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return makeErrorResponse(ErrorCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 2. 개발자가 직접 정의한 비즈니스 로직 예외 처리 (BusinessException)
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException: {}", e.getMessage());
        return makeErrorResponse(e.getErrorCode(), e.getMessage());
    }

    /**
     * 3. 잘못된 인자 전달 시 발생 (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException: {}", e.getMessage());
        return makeErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * 4. 데이터를 찾을 수 없는 경우 발생 (NoSuchElementException)
     */
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.error("handleNoSuchElementException: {}", e.getMessage());
        return makeErrorResponse(ErrorCode.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다.");
    }

    /**
     * 5. 보안 권한이 없는 경우 발생 (AccessDeniedException)
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException: {}", e.getMessage());
        return makeErrorResponse(ErrorCode.FORBIDDEN, "해당 요청에 대한 접근 권한이 없습니다.");
    }

    /**
     * 6. 그 외 서버 내부에서 발생하는 모든 예외 처리 (Exception)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException: ", e); // 상세 스택트레이스 로그 기록
        return makeErrorResponse(ErrorCode.INTERNAL_ERROR, "서버 내부 오류가 발생했습니다.");
    }
}
