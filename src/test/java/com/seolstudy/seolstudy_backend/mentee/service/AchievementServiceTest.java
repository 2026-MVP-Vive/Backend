package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.AchievementResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @InjectMocks
    private AchievementService achievementService;

    @Mock
    private TaskRepository taskRepository;

    @Test
    @DisplayName("성취도 조회 - 계산 로직 검증")
    void getAchievement() {
        // given
        Long menteeId = 1L;
        LocalDate start = LocalDate.of(2025, 1, 20);
        LocalDate end = LocalDate.of(2025, 1, 27);

        // Task Mocking
        Task koreanTask1 = createTask(Subject.KOREAN, 60, true);
        Task koreanTask2 = createTask(Subject.KOREAN, 30, false);
        Task englishTask1 = createTask(Subject.ENGLISH, 45, true);
        Task mathTask1 = createTask(Subject.MATH, 60, true);
        Task mathTask2 = createTask(Subject.MATH, 60, true);

        given(taskRepository.findAllByMenteeIdAndTaskDateBetween(eq(menteeId), eq(start), eq(end)))
                .willReturn(List.of(koreanTask1, koreanTask2, englishTask1, mathTask1, mathTask2));

        // when
        AchievementResponse response = achievementService.getAchievement(menteeId, start, end);

        // then
        assertThat(response.getPeriod().getStartDate()).isEqualTo(start);
        assertThat(response.getPeriod().getEndDate()).isEqualTo(end);

        // Overall Check
        // Total: 5, Completed: 4 (K1, E1, M1, M2), Time: 255
        // Rate: 4/5 * 100 = 80%
        AchievementResponse.Overall overall = response.getOverall();
        assertThat(overall.getTotalTasks()).isEqualTo(5);
        assertThat(overall.getCompletedTasks()).isEqualTo(4);
        assertThat(overall.getCompletionRate()).isEqualTo(80);
        assertThat(overall.getTotalStudyTime()).isEqualTo(255);

        // Subject Check
        // Korean: 2 tasks, 1 completed, 90 mins, 50%
        AchievementResponse.Achievement korean = response.getAchievements().stream()
                .filter(a -> a.getSubject() == Subject.KOREAN)
                .findFirst().orElseThrow();
        assertThat(korean.getTotalTasks()).isEqualTo(2);
        assertThat(korean.getCompletedTasks()).isEqualTo(1);
        assertThat(korean.getCompletionRate()).isEqualTo(50);
        assertThat(korean.getTotalStudyTime()).isEqualTo(90);

        // English: 1 task, 1 completed, 45 mins, 100%
        AchievementResponse.Achievement english = response.getAchievements().stream()
                .filter(a -> a.getSubject() == Subject.ENGLISH)
                .findFirst().orElseThrow();
        assertThat(english.getTotalTasks()).isEqualTo(1);
        assertThat(english.getCompletedTasks()).isEqualTo(1);
        assertThat(english.getCompletionRate()).isEqualTo(100);
        assertThat(english.getTotalStudyTime()).isEqualTo(45);
    }

    private Task createTask(Subject subject, int studyTime, boolean isConfirmed) {
        Task task = new Task();
        task.setSubject(subject);
        task.setStudyTime(studyTime);
        task.setMentorConfirmed(isConfirmed);
        return task;
    }
}
