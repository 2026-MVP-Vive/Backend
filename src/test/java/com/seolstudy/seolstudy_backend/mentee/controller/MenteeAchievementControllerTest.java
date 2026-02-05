package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.AchievementResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenteeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenteeAchievementControllerTest {

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
        private SecurityUtil securityUtil;

        @MockBean
        private MenteeReportService menteeReportService;

        @Test
        @DisplayName("성취도 조회 API")
        void getAchievement() throws Exception {
                // given
                Long menteeId = 1L;
                given(securityUtil.getCurrentUserId()).willReturn(menteeId);

                AchievementResponse response = AchievementResponse.builder()
                                .period(AchievementResponse.Period.builder()
                                                .startDate(LocalDate.of(2025, 1, 20))
                                                .endDate(LocalDate.of(2025, 1, 27))
                                                .build())
                                .build();

                given(achievementService.getAchievement(eq(menteeId), any(), any()))
                                .willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/mentee/achievement")
                                .param("startDate", "2025-01-20")
                                .param("endDate", "2025-01-27"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.period.startDate").value("2025-01-20"));
        }
}
