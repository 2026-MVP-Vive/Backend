package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.PlannerCompletion;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.PlannerCompletionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.PlannerCompletionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlannerServiceTest {

    @InjectMocks
    private PlannerService plannerService;

    @Mock
    private PlannerCompletionRepository plannerCompletionRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Test
    @DisplayName("플래너 마감 성공: 모든 할 일이 제출됨")
    void completeDailyPlanner_Success() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        // 이미 완료되지 않음
        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(false);

        // 할 일 존재
        Task task1 = new Task(menteeId, "Task 1", date, null, menteeId);
        task1.setId(10L);
        Task task2 = new Task(menteeId, "Task 2", date, null, menteeId);
        task2.setId(20L);
        given(taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date)).willReturn(List.of(task1, task2));

        // 모든 할 일 제출됨
        given(submissionRepository.existsByTaskId(10L)).willReturn(true);
        given(submissionRepository.existsByTaskId(20L)).willReturn(true);

        // 저장 모킹
        PlannerCompletion savedCompletion = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.now())
                .build();
        given(plannerCompletionRepository.save(any(PlannerCompletion.class))).willReturn(savedCompletion);

        // when
        PlannerCompletionResponse response = plannerService.completeDailyPlanner(menteeId, date);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getDate()).isEqualTo(date);
        verify(plannerCompletionRepository).save(any(PlannerCompletion.class));
    }

    @Test
    @DisplayName("플래너 마감 실패: 제출되지 않은 할 일이 있음")
    void completeDailyPlanner_Failure_IncompleteTasks() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(false);

        Task task1 = new Task(menteeId, "Task 1", date, null, menteeId);
        task1.setId(10L);
        Task task2 = new Task(menteeId, "Task 2", date, null, menteeId);
        task2.setId(20L);
        given(taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date)).willReturn(List.of(task1, task2));

        // task1은 제출됨, task2는 제출 안됨
        given(submissionRepository.existsByTaskId(10L)).willReturn(true);
        given(submissionRepository.existsByTaskId(20L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> plannerService.completeDailyPlanner(menteeId, date))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("아직 완료되지 않은 할 일이 있습니다");

        verify(plannerCompletionRepository, never()).save(any(PlannerCompletion.class));
    }

    @Test
    @DisplayName("플래너 마감 실패: 할 일이 하나도 없음")
    void completeDailyPlanner_Failure_NoTasks() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(false);
        given(taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> plannerService.completeDailyPlanner(menteeId, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("할 일이 없습니다");
    }

    @Test
    @DisplayName("플래너 마감: 이미 완료된 경우 (멱등성)")
    void completeDailyPlanner_AlreadyCompleted() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(true);

        PlannerCompletion existing = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.of(2025, 1, 27, 22, 0))
                .build();
        given(plannerCompletionRepository.findByMenteeIdAndPlanDate(menteeId, date)).willReturn(Optional.of(existing));

        // when
        PlannerCompletionResponse response = plannerService.completeDailyPlanner(menteeId, date);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getCompletedAt()).isEqualTo(existing.getCompletedAt());
        verify(plannerCompletionRepository, never()).save(any(PlannerCompletion.class));
    }
}
