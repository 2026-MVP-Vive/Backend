package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.PlannerCompletion;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyPlanDto;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
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
    public PlannerCompletionResponse completeDailyPlanner(Long menteeId, LocalDate date) {
        // 이미 완료된 상태인지 확인 (Optional, 멱등성 보장을 위해)
        if (plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)) {
            PlannerCompletion existing = plannerCompletionRepository.findByMenteeIdAndPlanDate(menteeId, date).get();
            return PlannerCompletionResponse.builder()
                    .date(existing.getPlanDate())
                    .completedAt(existing.getCompletedAt())
                    .status("COMPLETED")
                    .build();
        }

        // 해당 날짜의 모든 할 일 조회
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date);

        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 등록된 할 일이 없습니다.");

        }

        // 모든 할 일이 제출되었는지 확인
        // TODO: Optimize N+1 query if needed. Currently checking existence for each
        // task.
        for (Task task : tasks) {
            if (!submissionRepository.existsByTaskId(task.getId())) {
                throw new IllegalStateException("아직 완료되지 않은 할 일이 있습니다. 모든 할 일을 인증해주세요.");
            }
        }

        PlannerCompletion completion = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.now()) // Auditing may handle this, but setting for response consistency
                .build();

        PlannerCompletion saved = plannerCompletionRepository.save(completion);

        return PlannerCompletionResponse.builder()
                .date(saved.getPlanDate())
                .completedAt(saved.getCompletedAt() != null ? saved.getCompletedAt() : LocalDateTime.now())
                .status("COMPLETED")
                .build();
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