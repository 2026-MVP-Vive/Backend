package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.MenteeProfileResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class MenteeProfileController {

    private final MenteeProfileService menteeProfileService;

    @GetMapping("/profile")
    public ApiResponse<MenteeProfileResponse> getProfile() {
        // TODO: Get authenticated user ID from security context
        // For MVP, hardcoding to Mentee 1 (Min Yujin, id=2)
        Long menteeId = 2L;

        MenteeProfileResponse response = menteeProfileService.getMenteeProfile(menteeId);
        return ApiResponse.success(response);
    }
}
