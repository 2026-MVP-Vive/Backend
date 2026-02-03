package com.seolstudy.seolstudy_backend.mentee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seolstudy.seolstudy_backend.global.config.SecurityConfig;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenteeController.class)
@Import(SecurityConfig.class) // Import SecurityConfig to apply permitAll
class MenteeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenteeService menteeService;

    @MockBean
    private SecurityUtil securityUtil; // Mock the new dependency

    @Test
    @DisplayName("멘티 할 일 추가 성공")
    void addTask_success() throws Exception {
        // given
        Long menteeId = 2L;
        TaskRequest request = new TaskRequest();
        // Reflection to set private fields since no setter/all-args constructor in
        // Request DTO
        ReflectionTestUtils.setField(request, "title", "Test Task");
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2025, 1, 27));
        ReflectionTestUtils.setField(request, "subject", Subject.MATH);

        TaskResponse response = new TaskResponse(1L, "Test Task", LocalDate.of(2025, 1, 27), Subject.MATH, false);

        given(securityUtil.getCurrentUserId()).willReturn(menteeId); // Stub SecurityUtil
        given(menteeService.addTask(eq(menteeId), any(TaskRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/mentee/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("할 일 추가 실패 - 필수 값 누락")
    void addTask_validation_fail() throws Exception {
        // given
        TaskRequest request = new TaskRequest();
        // Title is null/empty, Date is null

        // when & then
        mockMvc.perform(post("/api/v1/mentee/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Expect 400
    }
}
