package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.MonthlyPlanResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Test
    @DisplayName("월간 계획표를 조회한다")
    void getMonthlyPlan() {
        // given
        Long menteeId = 1L;
        int year = 2025;
        int month = 1;

        Task task = new Task(menteeId, "Task", LocalDate.of(2025, 1, 15), null, 1L);
        // Simulate completed
        ReflectionTestUtils.setField(task, "isMentorConfirmed", true);

        given(taskRepository.findAllByMenteeIdAndTaskDateBetween(eq(menteeId), any(), any()))
                .willReturn(List.of(task));

        // when
        MonthlyPlanResponse response = taskService.getMonthlyPlan(menteeId, year, month);

        // then
        assertThat(response.getYear()).isEqualTo(2025);
        assertThat(response.getMonth()).isEqualTo(1);
        assertThat(response.getPlans()).hasSize(31); // Jan has 31 days

        var planDay15 = response.getPlans().get(14); // 0-index, so 14 is 15th
        assertThat(planDay15.getDate()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(planDay15.getTaskCount()).isEqualTo(1);
        assertThat(planDay15.getCompletedCount()).isEqualTo(1);
        assertThat(planDay15.isHasTask()).isTrue();
    }
}
