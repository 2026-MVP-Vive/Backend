package com.seolstudy.seolstudy_backend.mentor.controller;

// POST /mentor/students/{id}/feedbacks
// PUT  /mentor/feedbacks/{feedbackId}
// PUT  /mentor/students/{id}/feedbacks/overall

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorFeedbackCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorFeedbackUpdateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorOverallFeedbackRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorFeedbackCreateResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorFeedbackUpdateResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorOverallFeedbackGetResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorOverallFeedbackResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mentor")
@RequiredArgsConstructor
public class MentorFeedbackController {

    private final MentorFeedbackService mentorFeedbackService;

    @PostMapping("/students/{studentId}/feedbacks")
    public ApiResponse<MentorFeedbackCreateResponse> createFeedback(
            @PathVariable Long studentId,
            @RequestBody MentorFeedbackCreateRequest request
    ) {
        return ApiResponse.success(
                mentorFeedbackService.createFeedback(studentId, request)
        );
    }

    @PutMapping("/feedbacks/{feedbackId}")
    public ApiResponse<MentorFeedbackUpdateResponse> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody MentorFeedbackUpdateRequest request
    ) {
        return ApiResponse.success(
                mentorFeedbackService.updateFeedback(feedbackId, request)
        );
    }

    @PutMapping("/students/{studentId}/feedbacks/overall")
    public ApiResponse<MentorOverallFeedbackResponse> upsertOverallFeedback(
            @PathVariable Long studentId,
            @RequestBody MentorOverallFeedbackRequest request
    ) {
        return ApiResponse.success(
                mentorFeedbackService.upsertOverallFeedback(studentId, request)
        );
    }
    @GetMapping("/students/{studentId}/feedbacks/overall")
    public ApiResponse<MentorOverallFeedbackGetResponse> getOverallFeedback(
            @PathVariable Long studentId,
            @RequestParam String date
    ) {
        return ApiResponse.success(
                mentorFeedbackService.getOverallFeedback(studentId, date)
        );
    }


}
