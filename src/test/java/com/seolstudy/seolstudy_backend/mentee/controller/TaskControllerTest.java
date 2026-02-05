package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyPlanDto;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
import com.seolstudy.seolstudy_backend.mentee.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private SecurityUtil securityUtil;

    @Test
    @DisplayName("월간 계획표 조회 API")
    void getMonthlyPlan() throws Exception {
        // given
        Long menteeId = 2L;
        given(securityUtil.getCurrentUserId()).willReturn(menteeId);

        MonthlyPlanResponse response = MonthlyPlanResponse.builder()
                .year(2025)
                .month(1)
                .plans(List.of(
                        DailyPlanDto.builder()
                                .date(LocalDate.of(2025, 1, 1))
                                .dayOfWeek("WED")
                                .taskCount(5)
                                .completedCount(2)
                                .hasTask(true)
                                .build()))
                .build();

        given(taskService.getMonthlyPlan(menteeId, 2025, 1)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/mentee/monthly-plan")
                .param("year", "2025")
                .param("month", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.plans[0].date").value("2025-01-01"));
    }
}
