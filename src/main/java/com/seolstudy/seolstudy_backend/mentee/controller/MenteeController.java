package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class MenteeController {

    private final MenteeService menteeService;
    private final SecurityUtil securityUtil;

    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> addTask(@Valid @RequestBody TaskRequest request) {
        Long menteeId = securityUtil.getCurrentUserId();

        TaskResponse response = menteeService.addTask(menteeId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}
