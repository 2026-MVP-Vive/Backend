package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.ApiResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.SolutionListResponse;
import com.seolstudy.seolstudy_backend.mentee.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mentee/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ApiResponse<SolutionListResponse> getSolutions(@RequestParam(required = false) Subject subject) {
        Long currentMenteeId = securityUtil.getCurrentUserId();
        // If currentMenteeId is null or invalid, SecurityUtil might return null or
        // throw exception.
        // Assuming authenticated context.

        var solutions = solutionService.getSolutions(currentMenteeId, subject);
        return ApiResponse.success(new SolutionListResponse(solutions));
    }
}