package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.WeeklyReport;
import com.seolstudy.seolstudy_backend.mentee.domain.WeeklyReportSubject;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.WeeklyReportRepository;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorWeeklyReportCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorWeeklyReportCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorWeeklyReportService {

    private final UserRepository userRepository;
    private final WeeklyReportRepository weeklyReportRepository;

    @Transactional
    public MentorWeeklyReportCreateResponse createWeeklyReport(
            Long studentId,
            MentorWeeklyReportCreateRequest request
    ) {
        // 1️⃣ 기본 검증
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("시작일/종료일은 필수입니다.");
        }
        if (request.getOverallFeedback() == null || request.getOverallFeedback().isBlank()) {
            throw new IllegalArgumentException("전체 피드백은 필수입니다.");
        }

        // 2️⃣ 멘티 존재 확인
        userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // ⚠️ JWT 붙이기 전 임시 mentorId
        Long mentorId = studentId;

        // 3️⃣ 주차 계산 (ISO 기준)
        int weekNumber = request.getStartDate().get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());

        // 4️⃣ 중복 주차 방지
        if (weeklyReportRepository.existsByMenteeIdAndWeekNumber(studentId, weekNumber)) {
            throw new IllegalArgumentException("이미 해당 주차의 리포트가 존재합니다.");
        }

        // 5️⃣ WeeklyReport 생성
        WeeklyReport report = WeeklyReport.builder()
                .menteeId(studentId)
                .mentorId(mentorId)
                .weekNumber(weekNumber)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .summary(request.getSummary())
                .overallFeedback(request.getOverallFeedback())
                .build();

        // 6️⃣ 과목별 피드백
        if (request.getSubjectFeedbacks() != null) {
            request.getSubjectFeedbacks().forEach(sf -> {
                WeeklyReportSubject subjectReport =
                        WeeklyReportSubject.builder()
                                .weeklyReport(report)
                                .subject(Subject.valueOf(sf.getSubject()))
                                .completionRate(0)       // 현재 명세에 없으므로 기본값
                                .totalStudyTime(0)       // 동일
                                .feedback(sf.getFeedback())
                                .build();

                report.getSubjectReports().add(subjectReport);
            });
        }

        WeeklyReport saved = weeklyReportRepository.save(report);

        return new MentorWeeklyReportCreateResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getCreatedAt()
        );
    }
}

