package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.ApiResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
import com.seolstudy.seolstudy_backend.mentee.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecurityUtil securityUtil;

    @GetMapping("/monthly-plan")
    public ApiResponse<MonthlyPlanResponse> getMonthlyPlan(@RequestParam int year, @RequestParam int month) {
        Long currentMenteeId = securityUtil.getCurrentUserId();
        MonthlyPlanResponse response = taskService.getMonthlyPlan(currentMenteeId, year, month);
        return ApiResponse.success(response);
    }
}