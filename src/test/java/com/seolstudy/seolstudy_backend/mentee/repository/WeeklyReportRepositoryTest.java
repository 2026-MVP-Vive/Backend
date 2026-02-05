package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.WeeklyReport;
import com.seolstudy.seolstudy_backend.mentee.domain.WeeklyReportSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import com.seolstudy.seolstudy_backend.global.config.JpaConfig;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class WeeklyReportRepositoryTest {

        @Autowired
        private WeeklyReportRepository weeklyReportRepository;

        @Test
        @DisplayName("멘티 ID로 주간 리포트 목록을 주차 내림차순으로 조회한다")
        void findAllByMenteeIdOrderByWeekNumberDesc() {
                // given
                Long menteeId = 1L;
                WeeklyReport report1 = WeeklyReport.builder()
                                .menteeId(menteeId)
                                .mentorId(10L)
                                .weekNumber(3)
                                .title("3주차 리포트")
                                .startDate(LocalDate.of(2025, 1, 13))
                                .endDate(LocalDate.of(2025, 1, 19))
                                .overallFeedback("좋아요")
                                .build();

                WeeklyReport report2 = WeeklyReport.builder()
                                .menteeId(menteeId)
                                .mentorId(10L)
                                .weekNumber(4)
                                .title("4주차 리포트")
                                .startDate(LocalDate.of(2025, 1, 20))
                                .endDate(LocalDate.of(2025, 1, 26))
                                .overallFeedback("더 좋아요")
                                .build();

                weeklyReportRepository.saveAll(List.of(report1, report2));

                // when
                List<WeeklyReport> result = weeklyReportRepository.findAllByMenteeIdOrderByWeekNumberDesc(menteeId);

                // then
                assertThat(result).hasSize(2);
                assertThat(result.get(0).getWeekNumber()).isEqualTo(4);
                assertThat(result.get(1).getWeekNumber()).isEqualTo(3);
        }

        @Test
        @DisplayName("주간 리포트와 과목별 상세 리포트의 연관관계를 확인한다")
        void verifyWeeklyReportAndSubjectMapping() {
                // given
                Long menteeId = 1L;
                WeeklyReport report = WeeklyReport.builder()
                                .menteeId(menteeId)
                                .mentorId(10L)
                                .weekNumber(1)
                                .title("1주차 리포트")
                                .startDate(LocalDate.of(2025, 1, 1))
                                .endDate(LocalDate.of(2025, 1, 7))
                                .overallFeedback("전체 피드백")
                                .build();

                WeeklyReportSubject subject1 = WeeklyReportSubject.builder()
                                .weeklyReport(report)
                                .subject(Subject.KOREAN)
                                .completionRate(100)
                                .totalStudyTime(120)
                                .feedback("국어 굿")
                                .build();

                WeeklyReportSubject subject2 = WeeklyReportSubject.builder()
                                .weeklyReport(report)
                                .subject(Subject.MATH)
                                .completionRate(80)
                                .totalStudyTime(150)
                                .feedback("수학 분발")
                                .build();

                report.getSubjectReports().addAll(List.of(subject1, subject2));

                WeeklyReport savedReport = weeklyReportRepository.save(report);

                // when
                WeeklyReport foundReport = weeklyReportRepository.findById(savedReport.getId()).orElseThrow();

                // then
                assertThat(foundReport.getSubjectReports()).hasSize(2);
                assertThat(foundReport.getSubjectReports())
                                .extracting(s -> s.getSubject())
                                .containsExactlyInAnyOrder(Subject.KOREAN, Subject.MATH);
        }
}
