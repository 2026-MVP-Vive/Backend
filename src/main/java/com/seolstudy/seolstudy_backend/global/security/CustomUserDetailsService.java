package com.seolstudy.seolstudy_backend.global.security;

import com.seolstudy.seolstudy_backend.auth.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.seolstudy.seolstudy_backend.auth.repository.MemberRepository;

/**
 * 로그인 시도 시 유저 정보를 DB에서 찾고 유저 객체를 반환하는 클래스입니다.
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdOrLoginId) throws UsernameNotFoundException {
        Member member;

        // JWT 토큰에서 전달된 값이 숫자(ID)인 경우 ID로 조회
        try {
            Long userId = Long.parseLong(userIdOrLoginId);
            member = memberRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 ID를 가진 사용자를 찾을 수 없습니다: " + userId));
        } catch (NumberFormatException e) {
            // 숫자가 아닌 경우 loginId로 조회 (로그인 시)
            member = memberRepository.findByLoginId(userIdOrLoginId)
                    .orElseThrow(
                            () -> new UsernameNotFoundException("해당 로그인 ID를 가진 사용자를 찾을 수 없습니다: " + userIdOrLoginId));
        }

        // 2. 스프링 시큐리티가 이해할 수 있는 UserDetails 객체로 변환
        // username에 ID를 사용하여 SecurityUtil.getLoginUserId()와 호환
        return User.builder()
                .username(String.valueOf(member.getId())) //util 패키지의 getMemberId를 위해 UserDetails 객체의 username에 member_id를 삽입

                .password(member.getPassword()) // DB에 저장된 암호화된 비밀번호
                .roles(member.getRole().name()) // MENTEE, MENTOR 등 (ROLE_ prefix는 스프링이 자동 처리)
                .build();
    }
}