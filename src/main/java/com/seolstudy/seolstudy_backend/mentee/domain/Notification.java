package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "is_sent")
    private boolean isSent = false; // 기본값 false

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(Long userId, NotificationType type, String title, String body, Long relatedId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.relatedId = relatedId;
        this.isRead = false;
        this.isSent = false; // 생성 시 기본은 전송 전 상태
    }

    // 전송 성공 시 호출할 메서드
    public void markAsSent() {
        this.isSent = true;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
