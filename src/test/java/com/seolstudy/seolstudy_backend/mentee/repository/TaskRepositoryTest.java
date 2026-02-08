package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("local")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("날짜 범위로 태스크를 조회한다")
    void findAllByMenteeIdAndTaskDateBetween() {
        // given
        Long menteeId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        Task task1 = new Task(menteeId, "Task 1", LocalDate.of(2025, 1, 15), Subject.KOREAN, 1L);
        ReflectionTestUtils.setField(task1, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(task1, "updatedAt", LocalDateTime.now());

        Task task2 = new Task(menteeId, "Task 2", LocalDate.of(2025, 2, 1), Subject.MATH, 1L); // Out of range
        ReflectionTestUtils.setField(task2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(task2, "updatedAt", LocalDateTime.now());

        taskRepository.saveAll(List.of(task1, task2));

        // when
        List<Task> result = taskRepository.findAllByMenteeIdAndTaskDateBetween(menteeId, startDate, endDate);

        // then
        assertThat(result).hasSize(1)
                .extracting("title")
                .containsExactly("Task 1");
    }
}
