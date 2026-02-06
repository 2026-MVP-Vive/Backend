package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.SolutionDto;
import com.seolstudy.seolstudy_backend.mentee.service.SolutionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolutionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SolutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolutionService solutionService;

    @MockBean
    private SecurityUtil securityUtil;

    @Test
    @DisplayName("솔루션 목록 조회 API")
    void getSolutions() throws Exception {
        // given
        Long menteeId = 2L;
        given(securityUtil.getCurrentUserId()).willReturn(menteeId);

        SolutionDto dto = SolutionDto.builder()
                .id(1L)
                .title("Test Solution")
                .subject(Subject.KOREAN)
                .subjectName("국어")
                .build();
        given(solutionService.getSolutions(eq(menteeId), any())).willReturn(List.of(dto));

        // when & then
        mockMvc.perform(get("/api/v1/mentee/solutions")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.solutions[0].title").value("Test Solution"))
                .andExpect(jsonPath("$.data.solutions[0].subject").value("KOREAN"));
    }

    @Test
    @DisplayName("과목 필터링 솔루션 목록 조회 API")
    void getSolutionsWithSubject() throws Exception {
        // given
        Long menteeId = 2L;
        given(securityUtil.getCurrentUserId()).willReturn(menteeId);

        SolutionDto dto = SolutionDto.builder()
                .id(2L)
                .title("Math Solution")
                .subject(Subject.MATH)
                .subjectName("수학")
                .build();
        given(solutionService.getSolutions(eq(menteeId), eq(Subject.MATH))).willReturn(List.of(dto));

        // when & then
        mockMvc.perform(get("/api/v1/mentee/solutions")
                .param("subject", "MATH")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.solutions[0].title").value("Math Solution"));
    }
}
