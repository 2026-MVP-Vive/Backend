package com.seolstudy.seolstudy_backend.mentor.controller;

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorWeeklyReportCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorWeeklyReportCreateResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorWeeklyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// POST /mentor/students/{id}/weekly-reports
@RestController
@RequestMapping("/api/v1/mentor")
@RequiredArgsConstructor
public class MentorWeeklyReportController {
    private final MentorWeeklyReportService mentorWeeklyReportService;
    @PostMapping("/students/{studentId}/weekly-reports")
    public ApiResponse<MentorWeeklyReportCreateResponse> createWeeklyReport(
            @PathVariable Long studentId,
            @RequestBody MentorWeeklyReportCreateRequest request
    ) {
        return ApiResponse.success(
                mentorWeeklyReportService.createWeeklyReport(studentId, request)
        );
    }

}
