package com.seolstudy.seolstudy_backend.mentor.controller;

// GET    /mentor/students/{id}/solutions
// POST   /mentor/students/{id}/solutions
// PUT    /mentor/students/{id}/solutions/{solutionId}
// DELETE /mentor/students/{id}/solutions/{solutionId}

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorSolutionCreateResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentSolutionResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorSolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentor")
@RequiredArgsConstructor
public class MentorSolutionController {
    private final MentorSolutionService mentorSolutionService;
    @GetMapping("/students/{studentId}/solutions")
    public ApiResponse<MentorStudentSolutionResponse> getStudentSolutions(
            @PathVariable Long studentId,
            @RequestParam(required = false) String subject
    ) {
        return ApiResponse.success(
                mentorSolutionService.getStudentSolutions(studentId, subject)
        );
    }

    @PostMapping(
            value = "/students/{studentId}/solutions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<MentorSolutionCreateResponse> createSolution(
            @PathVariable Long studentId,

            @RequestParam("title") String title,

            @RequestParam("subject") Subject subject,

            @RequestPart(value = "materials", required = false)
            List<MultipartFile> materials
    ) {
        return ApiResponse.success(
                mentorSolutionService.createSolution(
                        studentId, title, subject, materials
                )
        );
    }


}
