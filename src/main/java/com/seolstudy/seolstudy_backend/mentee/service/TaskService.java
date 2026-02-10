package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
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
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FeedbackRepository feedbackRepository;
    private final TaskMaterialRepository taskMaterialRepository;
    private final FileRepository fileRepository;

    @Transactional
    public TaskResponse addTask(Long menteeId, TaskRequest request) {
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

        if (tasks.isEmpty()) {
            return DailyTaskResponse.builder()
                    .date(date)
                    .tasks(List.of())
                    .summary(DailyTaskResponse.TaskSummary.builder().build())
                    .build();
        }

        List<Long> taskIds = tasks.stream().map(Task::getId).toList();

        // Batch fetching to avoid N+1 problem
        Set<Long> tasksWithSubmission = submissionRepository.findAllByTaskIdIn(taskIds).stream()
                .map(Submission::getTaskId)
                .collect(Collectors.toSet());

        Set<Long> tasksWithFeedback = feedbackRepository.findAllByTaskIdIn(taskIds).stream()
                .map(Feedback::getTaskId)
                .collect(Collectors.toSet());

        Map<Long, Long> materialCounts = taskMaterialRepository.findAllByTaskIdIn(taskIds).stream()
                .collect(Collectors.groupingBy(TaskMaterial::getTaskId, Collectors.counting()));

        List<TaskResponse> taskResponses = tasks.stream().map(task -> {
            boolean hasSubmission = tasksWithSubmission.contains(task.getId());
            boolean hasFeedback = tasksWithFeedback.contains(task.getId());
            int materialCount = materialCounts.getOrDefault(task.getId(), 0L).intValue();

            return TaskResponse.of(task, hasSubmission, hasFeedback, materialCount);
        }).toList();

        // Calculate summary
        int total = tasks.size();
        int completed = (int) taskResponses.stream().filter(TaskResponse::isCompleted).count();
        int totalStudyTime = taskResponses.stream()
                .map(TaskResponse::getStudyTime)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

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

        // Fetch related data
        List<TaskMaterial> materials = taskMaterialRepository.findAllByTaskId(taskId);
        Submission submission = submissionRepository.findByTaskId(taskId);
        Feedback feedback = feedbackRepository.findByTaskId(taskId);

        return buildTaskDetailResponse(task, materials, submission, feedback);
    }

    private TaskDetailResponse buildTaskDetailResponse(Task task, List<TaskMaterial> materials,
                                                       Submission submission, Feedback feedback) {
        // Goal
        TaskDetailResponse.GoalDto goalDto = null;
        if (task.getSolution() != null) {
            goalDto = TaskDetailResponse.GoalDto.builder()
                    .id(task.getSolution().getId())
                    .title(task.getSolution().getTitle())
                    .subject(task.getSolution().getSubject())
                    .build();
        }

        // Materials
        List<Long> fileIds = materials.stream().map(TaskMaterial::getFileId).toList();
        List<File> files = fileRepository.findAllById(fileIds);
        List<TaskDetailResponse.FileDto> fileDtos = files.stream().map(f -> TaskDetailResponse.FileDto.builder()
                .id(f.getId())
                .fileName(f.getOriginalName())
                .fileType(f.getFileType())
                .fileSize(f.getFileSize())
                .downloadUrl("/api/v1/files/" + f.getId() + "/download")
                .build()).toList();

        // Submission
        TaskDetailResponse.SubmissionDto submissionDto = null;
        if (submission != null) {
            submissionDto = TaskDetailResponse.SubmissionDto.builder()
                    .id(submission.getId())
                    .imageUrl("/api/v1/files/" + submission.getFileId())
                    .submittedAt(submission.getSubmittedAt())
                    .build();
        }

        // Feedback
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
                .isUploadRequired(task.isUploadRequired())
                .isMentorAssigned(task.isMentorAssigned())
                .isMentorConfirmed(task.isMentorConfirmed())
                .isMenteeCompleted(task.isMenteeCompleted())
                .materials(fileDtos)
                .submission(submissionDto)
                .feedback(feedbackDto)
                .createdAt(task.getCreatedAt())
                .build();
    }

    @Transactional
    public UpdateStudyTimeResponse updateStudyTime(Long menteeId, Long taskId, Integer studyTime) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getMenteeId().equals(menteeId)) {
            throw new RuntimeException("Access denied");
        }

        task.updateStudyTime(studyTime);
        return new UpdateStudyTimeResponse(task.getId(), task.getStudyTime());
    }

    public MonthlyPlanResponse getMonthlyPlan(Long menteeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDateBetween(menteeId, startDate, endDate);
        Map<LocalDate, List<Task>> tasksByDate = tasks.stream()
                .collect(Collectors.groupingBy(Task::getTaskDate));

        List<DailyPlanDto> plans = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            List<Task> dailyTasks = tasksByDate.getOrDefault(date, List.of());

            int taskCount = dailyTasks.size();
            int completedCount = (int) dailyTasks.stream()
                    .filter(Task::isMentorConfirmed)
                    .count();

            plans.add(DailyPlanDto.builder()
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .toUpperCase())
                    .taskCount(taskCount)
                    .completedCount(completedCount)
                    .hasTask(taskCount > 0)
                    .build());
        }

        return MonthlyPlanResponse.builder()
                .year(year)
                .month(month)
                .plans(plans)
                .build();
    }

    @Transactional
    public TaskCompleteResponse toggleTaskCompletion(Long menteeId, Long taskId, Boolean completed) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getMenteeId().equals(menteeId)) {
            throw new RuntimeException("Access denied");
        }

        if (completed == null) {
            throw new IllegalArgumentException("completed 값은 필수입니다.");
        }

        if (completed) {
            task.setMenteeCompleted(true);
            task.setMenteeCompletedAt(java.time.LocalDateTime.now());
        } else {
            task.setMenteeCompleted(false);
            task.setMenteeCompletedAt(null);
        }

        return TaskCompleteResponse.builder()
                .id(task.getId())
                .isMenteeCompleted(task.isMenteeCompleted())
                .menteeCompletedAt(task.getMenteeCompletedAt())
                .build();
    }
}