package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyTaskResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.seolstudy.seolstudy_backend.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenteeServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository submissionRepository;

    @Mock
    private com.seolstudy.seolstudy_backend.mentee.repository.FeedbackRepository feedbackRepository;

    @Mock
    private com.seolstudy.seolstudy_backend.mentee.repository.TaskMaterialRepository taskMaterialRepository;

    @Mock
    private FileRepository fileRepository;

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

    @Test
    @DisplayName("일별 과제 목록 조회 성공")
    void getDailyTasks_success() {
        // given
        Long menteeId = 1L;
        String dateStr = "2025-01-27";
        LocalDate date = LocalDate.parse(dateStr);

        Task task1 = new Task(menteeId, "Task 1", date, Subject.ENGLISH, menteeId);
        ReflectionTestUtils.setField(task1, "id", 101L);
        ReflectionTestUtils.setField(task1, "studyTime", 60);

        Task task2 = new Task(menteeId, "Task 2", date, Subject.MATH, menteeId);
        ReflectionTestUtils.setField(task2, "id", 102L);
        // task2 has no studyTime

        List<Task> tasks = List.of(task1, task2);

        given(taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date)).willReturn(tasks);
        given(submissionRepository.existsByTaskId(101L)).willReturn(true); // Task 1 completed
        given(feedbackRepository.existsByTaskId(101L)).willReturn(false);
        given(taskMaterialRepository.countByTaskId(101L)).willReturn(1);

        given(submissionRepository.existsByTaskId(102L)).willReturn(false); // Task 2 not completed
        given(feedbackRepository.existsByTaskId(102L)).willReturn(false);
        given(taskMaterialRepository.countByTaskId(102L)).willReturn(0);

        // when
        DailyTaskResponse response = menteeService.getDailyTasks(menteeId, dateStr);

        // then
        assertThat(response.getDate()).isEqualTo(date);
        assertThat(response.getTasks()).hasSize(2);

        // Summary verification
        assertThat(response.getSummary().getTotal()).isEqualTo(2);
        assertThat(response.getSummary().getCompleted()).isEqualTo(1);
        assertThat(response.getSummary().getTotalStudyTime()).isEqualTo(60);

        // Task detail verification
        TaskResponse tr1 = response.getTasks().get(0);
        assertThat(tr1.getId()).isEqualTo(101L);
        assertThat(tr1.isCompleted()).isTrue();
        assertThat(tr1.getMaterialCount()).isEqualTo(1);

        TaskResponse tr2 = response.getTasks().get(1);
        assertThat(tr2.getId()).isEqualTo(102L);
        assertThat(tr2.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("할 일 상세 조회 서비스 로직 성공")
    void getTaskDetail_success() {
        // given
        Long menteeId = 1L;
        Long taskId = 100L;

        Task task = new Task(menteeId, "Detailed Task", LocalDate.now(), Subject.KOREAN, menteeId);
        ReflectionTestUtils.setField(task, "id", taskId);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        given(taskMaterialRepository.findAllByTaskId(taskId)).willReturn(List.of());
        given(submissionRepository.findByTaskId(taskId)).willReturn(null);
        given(feedbackRepository.findByTaskId(taskId)).willReturn(null);

        // when
        TaskDetailResponse response = menteeService.getTaskDetail(menteeId, taskId);

        // then
        assertThat(response.getId()).isEqualTo(taskId);
        assertThat(response.getTitle()).isEqualTo("Detailed Task");
        assertThat(response.getSubject()).isEqualTo(Subject.KOREAN);
    }

    @Test
    @DisplayName("공부 시간 기록 서비스 로직 성공")
    void updateStudyTime_success() {
        // given
        Long menteeId = 1L;
        Long taskId = 100L;
        Integer studyTime = 60;

        Task task = new Task(menteeId, "Study Task", LocalDate.now(), Subject.MATH, menteeId);
        ReflectionTestUtils.setField(task, "id", taskId);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when
        UpdateStudyTimeResponse response = menteeService.updateStudyTime(menteeId, taskId, studyTime);

        // then
        assertThat(response.getId()).isEqualTo(taskId);
        assertThat(response.getStudyTime()).isEqualTo(studyTime);
        assertThat(task.getStudyTime()).isEqualTo(studyTime);
    }
}
