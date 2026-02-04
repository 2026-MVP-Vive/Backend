package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeService;
import com.seolstudy.seolstudy_backend.mentee.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class MenteeController {

    private final MenteeService menteeService;
    private final SubmissionService submissionService;
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

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getDailyTasks(@RequestParam("date") String date) {
        Long menteeId = securityUtil.getCurrentUserId();
        DailyTaskResponse response = menteeService.getDailyTasks(menteeId, date);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskDetail(@PathVariable("taskId") Long taskId) {
        Long menteeId = securityUtil.getCurrentUserId();
        TaskDetailResponse response = menteeService.getTaskDetail(menteeId, taskId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/tasks/{taskId}/study-time")
    public ResponseEntity<Map<String, Object>> updateStudyTime(
            @PathVariable("taskId") Long taskId,
            @Valid @RequestBody UpdateStudyTimeRequest request) {
        Long menteeId = securityUtil.getCurrentUserId();
        UpdateStudyTimeResponse response = menteeService.updateStudyTime(menteeId, taskId, request.getStudyTime());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/tasks/{taskId}/submission")
    public ResponseEntity<Map<String, Object>> submitTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("image") MultipartFile image) {
        Long menteeId = securityUtil.getCurrentUserId();
        SubmissionResponse response = submissionService.submitTask(menteeId, taskId, image);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<Map<String, Object>> getDailyFeedbacks(@RequestParam("date") String date) {
        Long menteeId = securityUtil.getCurrentUserId();
        DailyFeedbackResponse response = menteeService.getDailyFeedbacks(menteeId, date);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/feedbacks/yesterday")
    public ResponseEntity<Map<String, Object>> getYesterdayFeedbacks() {
        Long menteeId = securityUtil.getCurrentUserId();
        YesterdayFeedbackResponse response = menteeService.getYesterdayFeedbacks(menteeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}
