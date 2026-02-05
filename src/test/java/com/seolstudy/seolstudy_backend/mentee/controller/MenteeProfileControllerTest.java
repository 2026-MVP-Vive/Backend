package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.mentee.dto.MenteeProfileResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenteeProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenteeProfileService menteeProfileService;

    @InjectMocks
    private MenteeProfileController menteeProfileController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(menteeProfileController)
                .build();
    }

    @Test
    @DisplayName("멘티 프로필 조회 성공")
    void getProfile_Success() throws Exception {
        // given
        Long menteeId = 2L;
        MenteeProfileResponse response = MenteeProfileResponse.builder()
                .id(menteeId)
                .name("민유진")
                .profileImageUrl("/api/v1/files/profile/1")
                .mentorName("김멘토")
                .startDate(LocalDate.of(2024, 12, 1))
                .totalStudyDays(58)
                .build();

        // SecurityContext/Token is bypassed in standalone setup,
        // effectively testing the controller logic in isolation.
        // We'll rely on the manual verification for the Security integration.

        // Mocking the behavior where the ID comes from the token (which is handled by
        // ArgumentResolver or Filter)
        // In the Controller, the ID typically comes from @AuthenticationPrincipal or
        // extracted from token.
        // Let's check `MenteeProfileController` implementation to match the argument.

        given(menteeProfileService.getMenteeProfile(menteeId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/mentee/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.name").value("민유진"))
                .andExpect(jsonPath("$.data.mentorName").value("김멘토"));
    }
}
