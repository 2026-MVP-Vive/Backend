package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyTaskResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.FeedbackRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskMaterialRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeService {

    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FeedbackRepository feedbackRepository;
    private final TaskMaterialRepository taskMaterialRepository;

    @Transactional
    public TaskResponse addTask(Long menteeId, TaskRequest request) {
        // Mentee ID is used as createdBy for self-assigned tasks
        Task task = new Task(
                menteeId,
                request.getTitle(),
                request.getDate(),
                request.getSubject(),
                menteeId);
        Task savedTask = taskRepository.save(task);
        return TaskResponse.of(savedTask, false, false, 0);
    }

    public DailyTaskResponse getDailyTasks(Long menteeId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date);

        int total = tasks.size();
        int completed = 0;
        int totalStudyTime = 0;

        List<TaskResponse> taskResponses = tasks.stream().map(task -> {
            boolean hasSubmission = submissionRepository.existsByTaskId(task.getId());
            boolean hasFeedback = feedbackRepository.existsByTaskId(task.getId());
            int materialCount = taskMaterialRepository.countByTaskId(task.getId());

            return TaskResponse.of(task, hasSubmission, hasFeedback, materialCount);
        }).toList();

        for (TaskResponse tr : taskResponses) {
            if (tr.isCompleted()) {
                completed++;
            }
            if (tr.getStudyTime() != null) {
                totalStudyTime += tr.getStudyTime();
            }
        }

        return DailyTaskResponse.builder()
                .date(date)
                .tasks(taskResponses)
                .summary(DailyTaskResponse.TaskSummary.builder()
                        .total(total)
                        .completed(completed)
                        .totalStudyTime(totalStudyTime)
                        .build())
                .build();
    }
}
