package com.seolstudy.seolstudy_backend.auth.service;

import com.seolstudy.seolstudy_backend.auth.dto.LoginRequestDto;
import com.seolstudy.seolstudy_backend.auth.dto.MemberDto;
import com.seolstudy.seolstudy_backend.auth.dto.RefreshTokenResponse;
import com.seolstudy.seolstudy_backend.auth.dto.TokenResponseDto;
import com.seolstudy.seolstudy_backend.auth.entity.RefreshToken;
import com.seolstudy.seolstudy_backend.auth.repository.RefreshTokenRepository;
import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.global.security.JwtTokenProvider;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 로직을 구현해둔 서비스 클래스입니다.
 * */

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto){
        User user = userRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(() -> new BusinessException("아이디를 찾을 수 없습니다.", ErrorCode.NOT_FOUND));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new BusinessException("비밀번호가 일치하지 않습니다.", ErrorCode.NOT_FOUND);
        }

        // 1. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(), user.getRole());

        // 2. 리프레시 토큰 DB 저장/업데이트 로직 추가
        // 기존에 이 유저의 리프레시 토큰이 있는지 확인합니다.
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        token -> {
                            // 이미 있으면 새 토큰과 만료일(14일 뒤)로 업데이트
                            token.updateToken(refreshToken, java.time.LocalDateTime.now().plusDays(14));
                        },
                        () -> {
                            // 없으면 새로 생성해서 저장
                            RefreshToken newRefreshToken = RefreshToken.builder()
                                    .user(user)
                                    .token(refreshToken)
                                    .expiresAt(java.time.LocalDateTime.now().plusDays(14))
                                    .build();
                            refreshTokenRepository.save(newRefreshToken);
                        }
                );

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .memberDto(MemberToMemberDto(user))
                .build();
    }

    @Transactional
    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        // 1. 토큰 자체의 유효성 검증 (만료 여부, 서명 확인)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("리프레시 토큰이 유효하지 않습니다.", ErrorCode.NOT_FOUND);
        }

        // 2. DB에 저장된 토큰인지 확인 (중요!)
        // 로그인 시 저장했던 DB 정보와 일치하는지 대조합니다.
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("존재하지 않는 리프레시 토큰입니다.", ErrorCode.NOT_FOUND));

        // 3. 사용자 정보 조회
        User user = userRepository.findById(storedToken.getUser().getId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND));

        // 4. 새로운 Access Token 생성 (Refresh Token은 그대로 사용)
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(3600) // 1시간
                .build();
    }

    @Transactional
    public void logout() {
        // SecurityUtil을 사용하여 현재 로그인한 유저의 ID를 가져옵니다.
        Long userId = securityUtil.getCurrentUserId();

        // 해당 유저의 리프레시 토큰이 있다면 삭제합니다.
        // (사용자님이 만드신 Repo의 deleteByUserId 혹은 findByUser 등을 활용)
        refreshTokenRepository.deleteByUserId(userId);
    }

    public MemberDto MemberToMemberDto(User user){
        /** 프로필을 따로 설정하지 않은 경우에는 기본 프로필 경로로 설정 */
        String profileImageUrl = "/file/profiles/default-profile.png";
        if(user.getProfileImageId() != null){
            profileImageUrl = fileService.getProfileImageUrl(user.getProfileImageId());
        }
        MemberDto memberDto = MemberDto.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole().toString())
                .profileImageUrl(profileImageUrl)
                .build();
        return memberDto;
    }
}
