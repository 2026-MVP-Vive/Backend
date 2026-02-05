package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "weekly_report_subjects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_report_subjects", columnNames = { "report_id", "subject" })
})
@Getter
@NoArgsConstructor
@ToString
public class WeeklyReportSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

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
