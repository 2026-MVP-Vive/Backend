package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "monthly_report_subjects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_monthly_report_subjects_unique", columnNames = { "monthly_report_id", "subject" })
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MonthlyReportSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_report_id", nullable = false)
    @ToString.Exclude
    private MonthlyReport monthlyReport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Column(name = "completion_rate", nullable = false)
    private Integer completionRate;

    @Column(name = "total_study_time", nullable = false)
    private Integer totalStudyTime;

    @Column(columnDefinition = "TEXT")
    private String feedback;
}
