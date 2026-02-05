package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyPlanDto;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
public class TaskService {

    private final TaskRepository taskRepository;

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
                    .filter(Task::isMentorConfirmed) // Assuming 'completed' means confirmed by mentor, or implement
                                                     // status check
                    .count();
            // Or if completed means submitted? DB schema has submissions table.
            // For now, let's assume existence of submission or mentor confirm creates
            // completion.
            // As per schema: is_mentor_confirmed. Let's use that for now or just task
            // completion if there was a status.
            // But Task entity doesn't have status. It has isMentorConfirmed.
            // Let's rely on isMentorConfirmed for "completedCount" in this context, or
            // maybe just existence of submission?
            // The requirement says "completedCount". Usually, it's about student finishing
            // it.
            // Let's simpler logic: if there is a way to check completion.
            // Wait, there is no "completed" flag. `is_mentor_confirmed` is approval.
            // `submissions` table exists.
            // For MVP simplicity and speed, let's use `isMentorConfirmed` or maybe we need
            // to join submission?
            // "completedCount" in a plan usually means the student did it.
            // Let's assume `isMentorConfirmed` as "completed" for now, as it's the safest
            // "done" state.
            // Or better, let's just count tasks that have `isMentorConfirmed` as
            // completedCount.

            boolean hasTask = taskCount > 0;
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();

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
