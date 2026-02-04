package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_mentee", uniqueConstraints = {
        @UniqueConstraint(name = "uk_mentor_mentee", columnNames = { "mentor_id", "mentee_id" })
})
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MentorMentee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MentorMentee(Long mentorId, Long menteeId) {
        this.mentorId = mentorId;
        this.menteeId = menteeId;
    }
}
