package com.seolstudy.seolstudy_backend.auth.service;

import com.seolstudy.seolstudy_backend.auth.dto.LoginRequestDto;
import com.seolstudy.seolstudy_backend.auth.entity.Member;
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

    @Transactional
    public String login(LoginRequestDto loginRequestDto){
        Member member = memberRepository.findByLoginId(loginRequestDto.getLoginId()).orElseThrow();

        //전달받은 비밀번호가 DB의 암호화된 비밀번호와 일치하는지 확인
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //회원 정보가 존재하면 Jwt를 생성 후 반환
        return jwtTokenProvider.createToken(member.getLoginId(), member.getRole());
    }
}