package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyReportDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeFeedbackService;
import com.seolstudy.seolstudy_backend.mentee.service.SubmissionService;
import com.seolstudy.seolstudy_backend.mentee.service.TaskService;
import com.seolstudy.seolstudy_backend.mentee.service.AchievementService;
import com.seolstudy.seolstudy_backend.mentee.service.MenteeReportService;
import com.seolstudy.seolstudy_backend.mentee.service.PlannerService;
import com.seolstudy.seolstudy_backend.mentee.service.ZoomMeetingService;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyReportListResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class MenteeController {

    private final TaskService taskService;
    private final MenteeFeedbackService menteeFeedbackService;
    private final SubmissionService submissionService;
    private final AchievementService achievementService;
    private final MenteeReportService menteeReportService;
    private final PlannerService plannerService;
    private final ZoomMeetingService zoomMeetingService;

    private final SecurityUtil securityUtil;

    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> addTask(@Valid @RequestBody TaskRequest request) {
        Long menteeId = securityUtil.getCurrentUserId();

        TaskResponse response = taskService.addTask(menteeId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getDailyTasks(@RequestParam("date") String date) {
        Long menteeId = securityUtil.getCurrentUserId();
        DailyTaskResponse response = taskService.getDailyTasks(menteeId, date);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskDetail(@PathVariable("taskId") Long taskId) {
        Long menteeId = securityUtil.getCurrentUserId();
        TaskDetailResponse response = taskService.getTaskDetail(menteeId, taskId);

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
        UpdateStudyTimeResponse response = taskService.updateStudyTime(menteeId, taskId, request.getStudyTime());

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

    @PatchMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, Object>> toggleTaskCompletion(
            @PathVariable("taskId") Long taskId,
            @RequestBody TaskCompleteRequest request) {
        Long menteeId = securityUtil.getCurrentUserId();
        TaskCompleteResponse response = taskService.toggleTaskCompletion(menteeId, taskId, request.getCompleted());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<Map<String, Object>> getDailyFeedbacks(@RequestParam("date") String date) {
        Long menteeId = securityUtil.getCurrentUserId();
        DailyFeedbackResponse response = menteeFeedbackService.getDailyFeedbacks(menteeId, date);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/feedbacks/yesterday")
    public ResponseEntity<Map<String, Object>> getYesterdayFeedbacks() {
        Long menteeId = securityUtil.getCurrentUserId();
        YesterdayFeedbackResponse response = menteeFeedbackService.getYesterdayFeedbacks(menteeId);

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

    @GetMapping("/weekly-reports")
    public ResponseEntity<Map<String, Object>> getWeeklyReports() {
        Long menteeId = securityUtil.getCurrentUserId();
        WeeklyReportListResponse response = menteeReportService.getWeeklyReports(menteeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/weekly-reports/{reportId}")
    public ResponseEntity<Map<String, Object>> getWeeklyReportDetail(@PathVariable("reportId") Long reportId) {
        Long menteeId = securityUtil.getCurrentUserId();
        WeeklyReportDetailResponse response = menteeReportService.getWeeklyReportDetail(menteeId, reportId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly-reports/{reportId}")
    public ResponseEntity<Map<String, Object>> getMonthlyReportDetail(@PathVariable("reportId") Long reportId) {
        Long menteeId = securityUtil.getCurrentUserId();
        MonthlyReportDetailResponse response = menteeReportService.getMonthlyReportDetail(menteeId, reportId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly-plan")
    public ResponseEntity<Map<String, Object>> getMonthlyPlan(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        Long menteeId = securityUtil.getCurrentUserId();
        MonthlyPlanResponse response = plannerService.getMonthlyPlan(menteeId, year, month);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly-reports")
    public ResponseEntity<Map<String, Object>> getMonthlyReports() {
        Long menteeId = securityUtil.getCurrentUserId();
        MonthlyReportListResponse response = menteeReportService.getMonthlyReports(menteeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/planner/{date}/complete")
    public ResponseEntity<Map<String, Object>> completePlanner(@PathVariable("date") String dateStr) {
        Long menteeId = securityUtil.getCurrentUserId();
        LocalDate date = LocalDate.parse(dateStr);
        PlannerCompletionResponse response = plannerService.completeDailyPlanner(menteeId, date);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/planner/{date}/status")
    public ResponseEntity<Map<String, Object>> getPlannerStatus(@PathVariable("date") String dateStr) {
        Long menteeId = securityUtil.getCurrentUserId();
        LocalDate date = LocalDate.parse(dateStr);
        PlannerCompletionResponse response = plannerService.getPlannerCompletionStatus(menteeId, date);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/zoom-meetings")
    public ResponseEntity<Map<String, Object>> getZoomMeetings(
            @RequestParam(value = "status", required = false) String status) {
        Long menteeId = securityUtil.getCurrentUserId();
        List<ZoomMeetingResponse> meetings = zoomMeetingService.getZoomMeetings(menteeId, status);

        Map<String, Object> data = new HashMap<>();
        data.put("meetings", meetings);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/zoom-meetings")
    public ResponseEntity<Map<String, Object>> requestZoomMeeting(@Valid @RequestBody ZoomMeetingRequest request) {
        Long menteeId = securityUtil.getCurrentUserId();
        ZoomMeetingResponse response = zoomMeetingService.requestZoomMeeting(menteeId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}