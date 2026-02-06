package com.seolstudy.seolstudy_backend.auth.repository;

import com.seolstudy.seolstudy_backend.auth.entity.RefreshToken;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰값으로 리프레시 토큰 정보 조회
    Optional<RefreshToken> findByToken(String token);

    // 유저 정보를 통해 기존 토큰이 있는지 확인 (로그인 시 필요)
    Optional<RefreshToken> findByUser(User user);

    // 만료된 토큰을 일괄 삭제하거나 특정 유저의 토큰을 삭제할 때 사용
    void deleteByUserId(Long id);
}
