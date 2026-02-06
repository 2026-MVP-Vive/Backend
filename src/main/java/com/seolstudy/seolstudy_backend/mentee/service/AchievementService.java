package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.AchievementResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AchievementService {

    private final TaskRepository taskRepository;

    public AchievementResponse getAchievement(Long menteeId, LocalDate startDate, LocalDate endDate) {
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDateBetween(menteeId, startDate, endDate);

        List<AchievementResponse.Achievement> achievements = new ArrayList<>();
        AchievementResponse.Overall overall = calculateOverall(tasks);

        for (Subject subject : Subject.values()) {
            achievements.add(calculateSubjectAchievement(tasks, subject));
        }

        return AchievementResponse.builder()
                .period(AchievementResponse.Period.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build())
                .achievements(achievements)
                .overall(overall)
                .build();
    }

    private AchievementResponse.Achievement calculateSubjectAchievement(List<Task> tasks, Subject subject) {
        List<Task> subjectTasks = tasks.stream()
                .filter(task -> task.getSubject() == subject)
                .toList();

        int totalTasks = subjectTasks.size();
        int completedTasks = (int) subjectTasks.stream()
                .filter(Task::isMentorConfirmed)
                .count();
        int totalStudyTime = subjectTasks.stream()
                .mapToInt(task -> task.getStudyTime() != null ? task.getStudyTime() : 0)
                .sum();
        int completionRate = totalTasks > 0 ? (int) ((double) completedTasks / totalTasks * 100) : 0;

        return AchievementResponse.Achievement.builder()
                .subject(subject)
                .subjectName(subject.getDescription())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionRate(completionRate)
                .totalStudyTime(totalStudyTime)
                .build();
    }

    private AchievementResponse.Overall calculateOverall(List<Task> tasks) {
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream()
                .filter(Task::isMentorConfirmed)
                .count();
        int totalStudyTime = tasks.stream()
                .mapToInt(task -> task.getStudyTime() != null ? task.getStudyTime() : 0)
                .sum();
        int completionRate = totalTasks > 0 ? (int) ((double) completedTasks / totalTasks * 100) : 0;

        return AchievementResponse.Overall.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionRate(completionRate)
                .totalStudyTime(totalStudyTime)
                .build();
    }
}