package com.seolstudy.seolstudy_backend.global.security;

import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 로그인 시도 시 유저 정보를 DB에서 찾고 유저 객체를 반환하는 클래스입니다.
 * */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // 1. DB에서 이메일로 사용자 조회 (명세서의 USERS 테이블)
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + loginId));

        // 2. 스프링 시큐리티가 이해할 수 있는 UserDetails 객체로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId())) //util 패키지의 getMemberId를 위해 UserDetails 객체의 username에 member_id를 삽입
                .password(user.getPassword()) // DB에 저장된 암호화된 비밀번호
                .roles(user.getRole().name()) // MENTEE, MENTOR 등 (ROLE_ prefix는 스프링이 자동 처리)
                .build();
    }
}