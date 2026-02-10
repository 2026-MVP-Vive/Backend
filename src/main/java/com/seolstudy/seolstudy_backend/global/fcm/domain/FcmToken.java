    package com.seolstudy.seolstudy_backend.global.fcm.domain;

    import jakarta.persistence.*;
    import lombok.AccessLevel;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "fcm_tokens")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public class FcmToken {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long userId; // 토큰 소유자 ID

        @Column(nullable = false, unique = true)
        private String token; // FCM 기기 토큰 값

        @Column(name = "last_used_at")
        private LocalDateTime lastUsedAt; // 토큰이 마지막으로 갱신/사용된 시간

        @Builder
        public FcmToken(Long userId, String token) {
            this.userId = userId;
            this.token = token;
            this.lastUsedAt = LocalDateTime.now();
        }

        // 기존 토큰의 사용 시간을 갱신하는 메서드
        public void updateLastUsed() {
            this.lastUsedAt = LocalDateTime.now();
        }
    }
