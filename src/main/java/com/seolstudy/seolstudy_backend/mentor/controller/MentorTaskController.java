package com.seolstudy.seolstudy_backend.mentor.controller;

// GET    /mentor/students/{id}/tasks
// POST   /mentor/students/{id}/tasks
// PUT    /mentor/students/{id}/tasks/{taskId}
// DELETE /mentor/students/{id}/tasks/{taskId}
// PATCH  /mentor/students/{id}/tasks/{taskId}/confirm

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorTaskCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentTaskResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorTaskCreateResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mentor")
@RequiredArgsConstructor
public class MentorTaskController {

    private final MentorTaskService mentorTaskService;

    @GetMapping("/students/{studentId}/tasks")
    public ApiResponse<MentorStudentTaskResponse> getStudentTasks(
            @PathVariable Long studentId,
            @RequestParam LocalDate date
    ) {
        return ApiResponse.success(
                mentorTaskService.getStudentTasks(studentId, date)
        );
    }
    @PostMapping(
            value = "/students/{studentId}/tasks",
            consumes = "application/json"
    )
    public ApiResponse<MentorTaskCreateResponse> createStudentTask(
            @PathVariable Long studentId,
            @RequestBody MentorTaskCreateRequest request
    ) {
        return ApiResponse.success(
                mentorTaskService.createStudentTask(studentId, request)
        );
    }

    @PostMapping(
            value = "/students/{studentId}/tasks",
            consumes = "multipart/form-data"
    )
    public ApiResponse<MentorTaskCreateResponse> createStudentTaskMultipart(
            @PathVariable Long studentId,

            @RequestParam String title,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            Long goalId,

            @RequestPart(required = false)
            List<MultipartFile> materials
    ) {
        return ApiResponse.success(
                mentorTaskService.createStudentTaskMultipart(
                        studentId, title, date, goalId, materials
                )
        );
    }


}

