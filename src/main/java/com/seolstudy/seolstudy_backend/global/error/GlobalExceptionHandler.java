package com.seolstudy.seolstudy_backend.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.seolstudy.seolstudy_backend.global.error.ErrorResponse;

/**
 * 전역 예외 처리 클래스입니다.
 * 해당 클래스에서 서버에서 발생하는 모든 종류의 예외를 예외 응답 반환 형식으로 변환한 후 프론트로 반환합니다.
 * 추가해야할 예외가 있는 경우 해당 클래스에 추가하시면 됩니다. ex) S3Exception...
 * */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 우리가 정의한 에러 코드에 맞춘 응답 생성 메서드
    private ResponseEntity<ErrorResponse> makeErrorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .success(false)
                        .data(null)
                        .message(message != null ? message : errorCode.getMessage())
                        .errorCode(errorCode.getCode())
                        .build());
    }

    //잘못된 인자 예외 (로그인 실패 등) -> BAD_REQUEST
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return makeErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    // 2. 404 리소스 없음 (데이터 조회 실패 등)
    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(java.util.NoSuchElementException e) {
        return makeErrorResponse(ErrorCode.NOT_FOUND, "요청하신 데이터를 찾을 수 없습니다.");
    }

    // 3. 그 외 모든 예외 -> INTERNAL_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        return makeErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage());
    }
}