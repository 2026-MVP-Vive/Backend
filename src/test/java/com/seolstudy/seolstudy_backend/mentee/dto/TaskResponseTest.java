package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaskResponseTest {

    @Test
    @DisplayName("Mentee task (isUploadRequired=false) should be completed when studyTime is set")
    void menteeTaskCompletion() {
        // given
        Task task = new Task(1L, "Mentee Task", LocalDate.now(), Subject.MATH, 1L);
        // Default isUploadRequired is false

        task.setStudyTime(60);

        // when
        TaskResponse response = TaskResponse.of(task, false, false, 0);

        // then
        assertThat(response.isUploadRequired()).isFalse();
        assertThat(response.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Mentee task should NOT be completed if studyTime is null")
    void menteeTaskIncomplete() {
        // given
        Task task = new Task(1L, "Mentee Task", LocalDate.now(), Subject.MATH, 1L);
        task.setStudyTime(null);

        // when
        TaskResponse response = TaskResponse.of(task, false, false, 0);

        // then
        assertThat(response.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("Mentor task (isUploadRequired=true) should NOT be completed by studyTime alone")
    void mentorTaskCompletionLimit() {
        // given
        Task task = new Task(1L, "Mentor Task", LocalDate.now(), Subject.MATH, 2L); // 2L is mentor
        task.setUploadRequired(true);
        task.setStudyTime(60);

        // when
        TaskResponse response = TaskResponse.of(task, false, false, 0); // hasSubmission = false

        // then
        assertThat(response.isUploadRequired()).isTrue();
        assertThat(response.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("Mentor task should be completed when submission exists")
    void mentorTaskCompletionWithSubmission() {
        // given
        Task task = new Task(1L, "Mentor Task", LocalDate.now(), Subject.MATH, 2L);
        task.setUploadRequired(true);
        task.setStudyTime(null);

        // when
        TaskResponse response = TaskResponse.of(task, true, false, 0); // hasSubmission = true

        // then
        assertThat(response.isCompleted()).isTrue();
    }
}
