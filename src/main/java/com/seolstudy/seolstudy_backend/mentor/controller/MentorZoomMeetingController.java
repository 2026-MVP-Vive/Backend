package com.seolstudy.seolstudy_backend.mentor.controller;

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.ZoomMeetingConfirmResponse;
import com.seolstudy.seolstudy_backend.mentor.service.MentorZoomMeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mentor")
@RequiredArgsConstructor
public class MentorZoomMeetingController {

    private final MentorZoomMeetingService mentorZoomMeetingService;

    @PatchMapping("/zoom-meetings/{meetingId}/confirm")
    public ApiResponse<ZoomMeetingConfirmResponse> confirmZoomMeeting(
            @PathVariable Long meetingId
    ) {
        return ApiResponse.success(
                mentorZoomMeetingService.confirmZoomMeeting(meetingId)
        );
    }
}
