package com.seolstudy.seolstudy_backend.mentor.controller;

// GET    /mentor/students/{id}/tasks
// POST   /mentor/students/{id}/tasks
// PUT    /mentor/students/{id}/tasks/{taskId}
// DELETE /mentor/students/{id}/tasks/{taskId}
// PATCH  /mentor/students/{id}/tasks/{taskId}/confirm

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentTaskResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}

