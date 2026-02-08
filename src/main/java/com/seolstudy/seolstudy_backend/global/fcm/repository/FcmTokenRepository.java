package com.seolstudy.seolstudy_backend.global.fcm.repository;

import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    // 유저의 모든 기기 토큰 조회 (여러 기기로 알림을 쏠 때 필요)
    List<FcmToken> findAllByUserId(Long userId);

    // 특정 토큰 존재 여부 확인 (중복 저장 방지)
    Optional<FcmToken> findByToken(String token);

    // 로그아웃 시 토큰 삭제
    void deleteByToken(String token);
}
