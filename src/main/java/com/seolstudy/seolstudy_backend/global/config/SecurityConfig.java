package com.seolstudy.seolstudy_backend.global.config;

import com.seolstudy.seolstudy_backend.global.security.JwtAuthenticationFilter;
import com.seolstudy.seolstudy_backend.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 시큐리티 필터 체인 설정 클래스입니다.
 * */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //CSRF, HTTP Basic, Form Login 비활성화 (Stateless 환경)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                //세션 정책 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //인가(Authorization) 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        /** 테스트에 한해 모든 사용자에게 모든 API 접근을 허용
                         *  실제 테스트 시 각 API별 권한 설정
                         * */
                        .requestMatchers("/api/v1/auth/**", "/api/v1/files/**", "api/v1/mentee/**"
                        , "api/v1/mentor/**").permitAll()
//                        .requestMatchers("/api/v1/mentor/**").hasRole("MENTOR") //권한 설
                        .anyRequest().authenticated() // 나머지 접근은 인증 필요
                )

                //JWT 필터 배치
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}