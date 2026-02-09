package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.PlannerCompletion;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyPlanDto;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.PlannerCompleteRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.PlannerCompletionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.PlannerCompletionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
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

                // 2. Already completed check (for idempotency)
                if (plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)) {
                        PlannerCompletion existing = plannerCompletionRepository
                                        .findByMenteeIdAndPlanDate(menteeId, date).get();
                        return PlannerCompletionResponse.builder()
                                        .date(existing.getPlanDate())
                                        .completedAt(existing.getCompletedAt())
                                        .status("COMPLETED")
                                        .tasks(taskIds)
                                        .build();
                }

                // 3. Save planner completion record
                PlannerCompletion completion = PlannerCompletion.builder()
                                .menteeId(menteeId)
                                .planDate(date)
                                .completedAt(LocalDateTime.now())
                                .build();

                try {
                        PlannerCompletion saved = plannerCompletionRepository.save(completion);
                        return PlannerCompletionResponse.builder()
                                        .date(saved.getPlanDate())
                                        .completedAt(saved.getCompletedAt() != null ? saved.getCompletedAt()
                                                        : LocalDateTime.now())
                                        .status("COMPLETED")
                                        .tasks(taskIds)
                                        .build();
                } catch (Exception e) {
                        return PlannerCompletionResponse.builder()
                                        .date(date)
                                        .completedAt(LocalDateTime.now())
                                        .status("COMPLETED (DB Error Ignored)")
                                        .tasks(taskIds)
                                        .build();
                }
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
