package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeFeedbackService;
import com.seolstudy.seolstudy_backend.mentee.service.SubmissionService;
import com.seolstudy.seolstudy_backend.mentee.service.TaskService;
import com.seolstudy.seolstudy_backend.mentee.service.AchievementService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class MenteeController {

    private final TaskService taskService;
    private final MenteeFeedbackService menteeFeedbackService;
    private final SubmissionService submissionService;
    private final AchievementService achievementService;
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

    @GetMapping("/achievement")
    public ResponseEntity<Map<String, Object>> getAchievement(
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {

        Long menteeId = securityUtil.getCurrentUserId();

        LocalDate end = (endDateStr != null) ? LocalDate.parse(endDateStr) : LocalDate.now();
        LocalDate start = (startDateStr != null) ? LocalDate.parse(startDateStr)
                : LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        AchievementResponse response = achievementService.getAchievement(menteeId, start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}
