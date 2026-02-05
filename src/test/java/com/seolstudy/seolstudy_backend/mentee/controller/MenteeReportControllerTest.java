package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.WeeklyReportDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.WeeklyReportListResponse;
import com.seolstudy.seolstudy_backend.mentee.service.AchievementService;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeFeedbackService;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeReportService;
import com.seolstudy.seolstudy_backend.mentee.service.SubmissionService;
import com.seolstudy.seolstudy_backend.mentee.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenteeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenteeReportControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskService taskService;

        @MockBean
        private MenteeFeedbackService menteeFeedbackService;

        @MockBean
        private SubmissionService submissionService;

        @MockBean
        private AchievementService achievementService;

        @MockBean
        private MenteeReportService menteeReportService;

        @MockBean
        private SecurityUtil securityUtil;

        @Test
        @DisplayName("주간 학습 리포트 목록 조회API")
        void getWeeklyReports() throws Exception {
                // given
                Long menteeId = 1L;
                given(securityUtil.getCurrentUserId()).willReturn(menteeId);

                WeeklyReportListResponse response = WeeklyReportListResponse.builder()
                                .reports(List.of(
                                                WeeklyReportListResponse.WeeklyReportItem.builder()
                                                                .id(1L)
                                                                .week(4)
                                                                .title("4주차 (25.12.21~27)")
                                                                .startDate(LocalDate.of(2025, 12, 21))
                                                                .endDate(LocalDate.of(2025, 12, 27))
                                                                .isAvailable(true)
                                                                .build(),
                                                WeeklyReportListResponse.WeeklyReportItem.builder()
                                                                .id(2L)
                                                                .week(3)
                                                                .title("3주차 (25.12.14~20)")
                                                                .startDate(LocalDate.of(2025, 12, 14))
                                                                .endDate(LocalDate.of(2025, 12, 20))
                                                                .isAvailable(true)
                                                                .build()))
                                .build();

                given(menteeReportService.getWeeklyReports(menteeId)).willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/mentee/weekly-reports"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.reports[0].week").value(4))
                                .andExpect(jsonPath("$.data.reports[0].title").value("4주차 (25.12.21~27)"))
                                .andExpect(jsonPath("$.data.reports[1].week").value(3));
        }

        @Test
        @DisplayName("주간 학습 리포트 상세 조회 API")
        void getWeeklyReportDetail() throws Exception {
                // given
                Long menteeId = 1L;
                Long reportId = 1L;
                given(securityUtil.getCurrentUserId()).willReturn(menteeId);

                WeeklyReportDetailResponse response = WeeklyReportDetailResponse.builder()
                                .id(reportId)
                                .week(4)
                                .title("4주차 (25.12.21~27)")
                                .startDate(LocalDate.of(2024, 12, 21))
                                .endDate(LocalDate.of(2024, 12, 27))
                                .summary("이번 주 전체적으로 잘 수행했습니다.")
                                .overallFeedback("전체적으로 학습 루틴이 잘 잡혀가고 있습니다.")
                                .createdAt(LocalDate.of(2024, 12, 28))
                                .subjectReports(List.of(
                                                WeeklyReportDetailResponse.SubjectReportItem.builder()
                                                                .subject("KOREAN")
                                                                .subjectName("국어")
                                                                .completionRate(90)
                                                                .totalStudyTime(180)
                                                                .feedback("문학 영역 집중 학습 잘 진행됨")
                                                                .build()))
                                .build();

                given(menteeReportService.getWeeklyReportDetail(menteeId, reportId)).willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/mentee/weekly-reports/" + reportId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.week").value(4))
                                .andExpect(jsonPath("$.data.subjectReports[0].subject").value("KOREAN"))
                                .andExpect(jsonPath("$.data.subjectReports[0].subjectName").value("국어"));
        }
}
