package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... (fields)

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id")
    private Solution solution;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject")
    private Subject subject;

    @Column(name = "study_time")
    private Integer studyTime;

    @Column(name = "is_upload_required", nullable = false)
    private boolean isUploadRequired;

    @Column(name = "is_mentor_assigned", nullable = false)
    private boolean isMentorAssigned;

    @Column(name = "is_mentor_confirmed", nullable = false)
    private boolean isMentorConfirmed;

    @Column(name = "is_mentee_completed", nullable = false)
    private boolean isMenteeCompleted;

    @Column(name = "mentee_completed_at")
    private LocalDateTime menteeCompletedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Task(Long menteeId, String title, LocalDate taskDate, Subject subject, Long createdBy) {
        this.menteeId = menteeId;
        this.title = title;
        this.taskDate = taskDate;
        this.subject = subject;
        this.createdBy = createdBy;
        this.isUploadRequired = false; // Default for Mentee created tasks
        this.isMentorAssigned = false;
        this.isMentorConfirmed = false;
    }

    public void updateStudyTime(Integer studyTime) {
        if (studyTime == null || studyTime < 0) {
            throw new IllegalArgumentException("Study time cannot be null or negative");
        }
        this.studyTime = studyTime;
    }
}
