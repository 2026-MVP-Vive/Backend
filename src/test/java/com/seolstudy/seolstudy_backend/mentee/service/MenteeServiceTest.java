package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenteeServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private MenteeService menteeService;

    @Test
    @DisplayName("할 일 추가 서비스 로직 성공")
    void addTask_success() {
        // given
        Long menteeId = 1L;
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "Service Test");
        ReflectionTestUtils.setField(request, "date", LocalDate.now());
        ReflectionTestUtils.setField(request, "subject", Subject.ENGLISH);

        // Mocking saved entity with an ID
        Task savedTask = new Task(menteeId, "Service Test", LocalDate.now(), Subject.ENGLISH, menteeId);
        ReflectionTestUtils.setField(savedTask, "id", 100L);

        given(taskRepository.save(any(Task.class))).willReturn(savedTask);

        // when
        TaskResponse response = menteeService.addTask(menteeId, request);

        // then
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getTitle()).isEqualTo("Service Test");
        assertThat(response.getSubject()).isEqualTo(Subject.ENGLISH);
        assertThat(response.isMentorAssigned()).isFalse();
    }
}
