package com.seolstudy.seolstudy_backend.mentor.controller;

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// GET /api/v1/mentor/students
@RestController
@RequestMapping("/api/v1/mentor")
@RequiredArgsConstructor
public class MentorStudentController {

    private final MentorStudentService mentorStudentService;

    @GetMapping("/students")
    public ApiResponse<MentorStudentResponse> getStudents() {
        return ApiResponse.success(mentorStudentService.getMyStudents());
    }
}
