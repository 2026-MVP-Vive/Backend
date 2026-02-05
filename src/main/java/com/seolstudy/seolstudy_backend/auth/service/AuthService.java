package com.seolstudy.seolstudy_backend.auth.service;

import com.seolstudy.seolstudy_backend.auth.dto.LoginRequestDto;
import com.seolstudy.seolstudy_backend.auth.dto.MemberDto;
import com.seolstudy.seolstudy_backend.auth.dto.TokenResponseDto;
import com.seolstudy.seolstudy_backend.auth.entity.Member;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.global.security.JwtTokenProvider;
import com.seolstudy.seolstudy_backend.auth.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto){
        Member member = memberRepository.findByLoginId(loginRequestDto.getLoginId()).orElseThrow();

        //전달받은 비밀번호가 DB의 암호화된 비밀번호와 일치하는지 확인
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return TokenResponseDto.builder()
                .accessToken(jwtTokenProvider.createToken(member.getLoginId(), member.getRole()))
                .refreshToken(null)
                .tokenType("Bearer")
                .expiresIn(3600)
                .memberDto(MemberToMemberDto(member))
                .build();
    }

    public MemberDto MemberToMemberDto(Member member){
        /** 프로필을 따로 설정하지 않은 경우에는 기본 프로필 경로로 설정 */
        String profileImageUrl = "/file/profiles/default-profile.png";
        if(member.getProfileImageId() != null){
            profileImageUrl = fileService.getProfileImageUrl(member.getProfileImageId());
        }
        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole().toString())
                .profileImageUrl(profileImageUrl)
                .build();
        return memberDto;
    }
}