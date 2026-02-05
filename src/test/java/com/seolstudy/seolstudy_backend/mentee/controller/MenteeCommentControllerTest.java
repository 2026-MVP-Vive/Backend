package com.seolstudy.seolstudy_backend.mentee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenteeCommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenteeCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenteeCommentService menteeCommentService;

    @MockBean
    private SecurityUtil securityUtil;

    @Test
    @DisplayName("코멘트/질문을 등록한다")
    void createComment() throws Exception {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);
        CommentCreateRequest request = new CommentCreateRequest("오늘 국어 문제 풀다가 막히는 부분이 있었어요", date);

        given(securityUtil.getCurrentUserId()).willReturn(menteeId);

        CommentCreateResponse response = CommentCreateResponse.builder()
                .id(401L)
                .content("오늘 국어 문제 풀다가 막히는 부분이 있었어요")
                .date(date)
                .createdAt(LocalDateTime.of(2025, 1, 27, 15, 0))
                .build();

        given(menteeCommentService.createComment(eq(menteeId), any(CommentCreateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/mentee/comments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(401))
                .andExpect(jsonPath("$.data.content").value("오늘 국어 문제 풀다가 막히는 부분이 있었어요"))
                .andExpect(jsonPath("$.data.date").value("2025-01-27"));
    }
}
