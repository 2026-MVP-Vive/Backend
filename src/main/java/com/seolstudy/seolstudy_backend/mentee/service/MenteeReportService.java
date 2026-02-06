package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.MonthlyReport;
import com.seolstudy.seolstudy_backend.mentee.domain.WeeklyReport;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyReportDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.WeeklyReportDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.WeeklyReportListResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.MonthlyReportRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.WeeklyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeReportService {

        private final WeeklyReportRepository weeklyReportRepository;
        private final MonthlyReportRepository monthlyReportRepository;

        public WeeklyReportListResponse getWeeklyReports(Long menteeId) {
                List<WeeklyReport> reports = weeklyReportRepository.findAllByMenteeIdOrderByWeekNumberDesc(menteeId);

                List<WeeklyReportListResponse.WeeklyReportItem> reportItems = reports.stream()
                                .map(report -> WeeklyReportListResponse.WeeklyReportItem.builder()
                                                .id(report.getId())
                                                .week(report.getWeekNumber())
                                                .title(report.getTitle())
                                                .startDate(report.getStartDate())
                                                .endDate(report.getEndDate())
                                                .isAvailable(true) // Assuming all retrieved reports are available
                                                .build())
                                .collect(Collectors.toList());

                return WeeklyReportListResponse.builder()
                                .reports(reportItems)
                                .build();
        }

        public WeeklyReportDetailResponse getWeeklyReportDetail(Long menteeId, Long reportId) {
                WeeklyReport report = weeklyReportRepository.findById(reportId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 리포트를 찾을 수 없습니다."));

                if (!report.getMenteeId().equals(menteeId)) {
                        throw new IllegalArgumentException("자신의 리포트만 조회할 수 있습니다.");
                }

                List<WeeklyReportDetailResponse.SubjectReportItem> subjects = report.getSubjectReports().stream()
                                .map(s -> WeeklyReportDetailResponse.SubjectReportItem.builder()
                                                .subject(s.getSubject().name())
                                                .subjectName(s.getSubject().getDescription())
                                                .completionRate(s.getCompletionRate())
                                                .totalStudyTime(s.getTotalStudyTime())
                                                .feedback(s.getFeedback())
                                                .build())
                                .collect(Collectors.toList());

                return WeeklyReportDetailResponse.builder()
                                .id(report.getId())
                                .week(report.getWeekNumber())
                                .title(report.getTitle())
                                .startDate(report.getStartDate())
                                .endDate(report.getEndDate())
                                .summary(report.getSummary())
                                .overallFeedback(report.getOverallFeedback())
                                .createdAt(report.getCreatedAt().toLocalDate())
                                .subjectReports(subjects)
                                .build();
        }

        public MonthlyReportDetailResponse getMonthlyReportDetail(Long menteeId, Long reportId) {
                MonthlyReport report = monthlyReportRepository.findById(reportId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 리포트를 찾을 수 없습니다."));

                if (!report.getMenteeId().equals(menteeId)) {
                        throw new IllegalArgumentException("자신의 리포트만 조회할 수 있습니다.");
                }

                // Calculate start and end date of the month
                LocalDate startDate = LocalDate.of(report.getReportYear(), report.getReportMonth(), 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

                List<MonthlyReportDetailResponse.SubjectReportDto> subjects = report.getSubjectReports().stream()
                                .map(s -> MonthlyReportDetailResponse.SubjectReportDto.builder()
                                                .subject(s.getSubject().name())
                                                .subjectName(s.getSubject().getDescription())
                                                .completionRate(s.getCompletionRate())
                                                .totalStudyTime(s.getTotalStudyTime())
                                                .feedback(s.getFeedback())
                                                .build())
                                .collect(Collectors.toList());

                return MonthlyReportDetailResponse.builder()
                                .id(report.getId())
                                .reportMonth(report.getReportMonth())
                                .title(report.getTitle())
                                .startDate(startDate)
                                .endDate(endDate)
                                .summary(report.getSummary())
                                .overallFeedback(report.getOverallFeedback())
                                .createdAt(report.getCreatedAt().toLocalDate())
                                .subjectReports(subjects)
                                .build();
        }
}
