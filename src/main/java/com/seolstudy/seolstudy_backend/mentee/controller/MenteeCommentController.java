package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mentee/comments")
@RequiredArgsConstructor
public class MenteeCommentController {

    private final MenteeCommentService menteeCommentService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ApiResponse<CommentCreateResponse> createComment(@RequestBody CommentCreateRequest request) {
        Long menteeId = securityUtil.getCurrentUserId();
        CommentCreateResponse response = menteeCommentService.createComment(menteeId, request);
        return ApiResponse.success(response);
    }
}