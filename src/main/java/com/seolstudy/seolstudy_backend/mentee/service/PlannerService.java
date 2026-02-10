package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.mentee.domain.*;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
import com.seolstudy.seolstudy_backend.mentee.repository.*;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorTaskCreateMultipartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlannerService {

        private final PlannerCompletionRepository plannerCompletionRepository;
        private final TaskRepository taskRepository;
        private final SubmissionRepository submissionRepository;
        private final NotificationService notificationService;
        private final MentorMenteeRepository mentorMenteeRepository;
        private final UserRepository userRepository;

        @Transactional
        public PlannerCompletionResponse completeDailyPlanner(Long menteeId, LocalDate date,
                        PlannerCompleteRequest request) {
                // 1. Update specified tasks as completed
                List<Long> taskIds = request.getTasks();
                if (taskIds != null && !taskIds.isEmpty()) {
                        List<Task> tasksToUpdate = taskRepository.findAllById(taskIds);
                        for (Task task : tasksToUpdate) {
                                if (task.getMenteeId().equals(menteeId)) {
                                        task.setMenteeCompleted(true);
                                        task.setMenteeCompletedAt(LocalDateTime.now());
                                }
                        }
                        taskRepository.saveAll(tasksToUpdate);
                }

                // 2. Save planner completion record (Mentee Action)
                // 멘토 확인 여부와 상관없이 멘티가 마감 요청을 하면 완료 처리 (피드백 요청)
                if (!plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)) {
                        PlannerCompletion completion = PlannerCompletion.builder()
                                        .menteeId(menteeId)
                                        .planDate(date)
                                        .build();
                        try {
                                PlannerCompletion saved = plannerCompletionRepository.save(completion);

                                // 멘토 정보 탐색
                                MentorMentee mentor = mentorMenteeRepository.findByMenteeId(menteeId)
                                                .orElseThrow(() -> new BusinessException("담당 멘토를 찾을 수 없습니다.",
                                                                ErrorCode.NOT_FOUND));
                                // 멘티 정보 탐색
                                User user = userRepository.findById(menteeId)
                                                .orElseThrow(() -> new BusinessException("멘티 정보를 찾을 수 없습니다,",
                                                                ErrorCode.NOT_FOUND));
                                notificationService.createNotification(
                                                mentor.getMentorId(),
                                                NotificationType.TASK_COMPLETED,
                                                user.getName() + " 학생의 플레너 마감 요청 접수",
                                                "담당 멘티의 플레너 마감 요청이 접수되었습니다.",
                                                saved.getId());
                        } catch (Exception e) {
                                // Fallback for DB errors or Notification errors
                                // 이미 존재하거나 기타 에러시 로그를 남기거나 무시 (idempotency)
                        }
                }

                // 3. Return COMPLETED status
                // DB 조회를 다시 해서 정확한 시간을 가져오거나, 현재 시간으로 반환
                PlannerCompletion existing = plannerCompletionRepository
                                .findByMenteeIdAndPlanDate(menteeId, date)
                                .orElse(null);

                LocalDateTime completedAt = (existing != null) ? existing.getCompletedAt() : LocalDateTime.now();

                return PlannerCompletionResponse.builder()
                                .date(date)
                                .completedAt(completedAt)
                                .status("COMPLETED")
                                .tasks(taskIds)
                                .build();
        }

        public PlannerCompletionResponse getPlannerCompletionStatus(Long menteeId, LocalDate date) {
                return plannerCompletionRepository.findByMenteeIdAndPlanDate(menteeId, date)
                                .map(completion -> PlannerCompletionResponse.builder()
                                                .date(completion.getPlanDate())
                                                .completedAt(completion.getCompletedAt())
                                                .status("COMPLETED")
                                                .build())
                                .orElse(PlannerCompletionResponse.builder()
                                                .date(date)
                                                .completedAt(null)
                                                .status("PENDING")
                                                .build());
        }

        public MonthlyPlanResponse getMonthlyPlan(Long menteeId, int year, int month) {
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();

                List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDateBetween(menteeId, startDate, endDate);

                Map<LocalDate, List<Task>> tasksByDate = tasks.stream()
                                .collect(Collectors.groupingBy(Task::getTaskDate));

                List<DailyPlanDto> plans = new ArrayList<>();
                for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                        LocalDate date = yearMonth.atDay(day);
                        List<Task> dailyTasks = tasksByDate.getOrDefault(date, List.of());

                        int taskCount = dailyTasks.size();

                        int completedCount = (int) dailyTasks.stream()
                                        .filter(Task::isMentorConfirmed)
                                        .count();

                        boolean hasTask = taskCount > 0;
                        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                        .toUpperCase();

                        plans.add(DailyPlanDto.builder()
                                        .date(date)
                                        .dayOfWeek(dayOfWeek)
                                        .taskCount(taskCount)
                                        .completedCount(completedCount)
                                        .hasTask(hasTask)
                                        .build());
                }

                return MonthlyPlanResponse.builder()
                                .year(year)
                                .month(month)
                                .plans(plans)
                                .build();
        }
}
