package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "overall_feedbacks", uniqueConstraints = {
        @UniqueConstraint(name = "uk_overall_feedbacks_mentee_date", columnNames = { "mentee_id", "feedback_date" })
})
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OverallFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "feedback_date", nullable = false)
    private LocalDate feedbackDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public OverallFeedback(Long menteeId, Long mentorId, LocalDate feedbackDate, String content) {
        this.menteeId = menteeId;
        this.mentorId = mentorId;
        this.feedbackDate = feedbackDate;
        this.content = content;
    }
}
