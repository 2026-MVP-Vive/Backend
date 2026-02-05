package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.file.domain.File;
import com.seolstudy.seolstudy_backend.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.*;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
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
    private final FileRepository fileRepository;

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

    public TaskDetailResponse getTaskDetail(Long menteeId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getMenteeId().equals(menteeId)) {
            throw new RuntimeException("Access denied");
        }

        // 1. Goal
        TaskDetailResponse.GoalDto goalDto = null;
        if (task.getSolution() != null) {
            goalDto = TaskDetailResponse.GoalDto.builder()
                    .id(task.getSolution().getId())
                    .title(task.getSolution().getTitle())
                    .subject(task.getSolution().getSubject())
                    .build();
        }

        // 2. Materials
        List<TaskMaterial> materials = taskMaterialRepository.findAllByTaskId(taskId);
        List<Long> fileIds = materials.stream().map(TaskMaterial::getFileId).toList();
        List<File> files = fileRepository.findAllById(fileIds);

        List<TaskDetailResponse.FileDto> fileDtos = files.stream().map(f -> TaskDetailResponse.FileDto.builder()
                .id(f.getId())
                .fileName(f.getOriginalName())
                .fileType(f.getFileType())
                .fileSize(f.getFileSize())
                .downloadUrl("/api/v1/files/" + f.getId() + "/download")
                .build()).toList();

        // 3. Submission
        Submission submission = submissionRepository.findByTaskId(taskId);
        TaskDetailResponse.SubmissionDto submissionDto = null;
        if (submission != null) {
            submissionDto = TaskDetailResponse.SubmissionDto.builder()
                    .id(submission.getId())
                    .imageUrl("/api/v1/files/" + submission.getFileId())
                    .submittedAt(submission.getSubmittedAt())
                    .build();
        }

        // 4. Feedback
        Feedback feedback = feedbackRepository.findByTaskId(taskId);
        TaskDetailResponse.FeedbackDto feedbackDto = null;
        if (feedback != null) {
            feedbackDto = TaskDetailResponse.FeedbackDto.builder()
                    .id(feedback.getId())
                    .content(feedback.getContent())
                    .summary(feedback.getSummary())
                    .isImportant(feedback.isImportant())
                    .createdAt(feedback.getCreatedAt())
                    .build();
        }

        return TaskDetailResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .date(task.getTaskDate())
                .subject(task.getSubject())
                .subjectName(task.getSubject() != null ? task.getSubject().getDescription() : null)
                .goal(goalDto)
                .studyTime(task.getStudyTime())
                .isMentorAssigned(task.isMentorAssigned())
                .isMentorConfirmed(task.isMentorConfirmed())
                .materials(fileDtos)
                .submission(submissionDto)
                .feedback(feedbackDto)
                .createdAt(task.getCreatedAt())
                .build();
    }

    @Transactional
    public UpdateStudyTimeResponse updateStudyTime(Long menteeId, Long taskId, Integer studyTime) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("해당 과제를 찾을 수 없습니다.", ErrorCode.NOT_FOUND));

        if (!task.getMenteeId().equals(menteeId)) {
            throw new BusinessException("접근 권한이 없습니다.", ErrorCode.FORBIDDEN);
        }

        task.updateStudyTime(studyTime);
        return new UpdateStudyTimeResponse(task.getId(), task.getStudyTime());
    }
}
