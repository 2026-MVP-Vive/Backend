package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "monthly_reports", uniqueConstraints = {
        @UniqueConstraint(name = "uk_monthly_reports_unique", columnNames = { "mentee_id", "report_year",
                "report_month" })
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "overall_feedback", nullable = false, columnDefinition = "TEXT")
    private String overallFeedback;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "monthlyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<MonthlyReportSubject> subjectReports = new ArrayList<>();
}