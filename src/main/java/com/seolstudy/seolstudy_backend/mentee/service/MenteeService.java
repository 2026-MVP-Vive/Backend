package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.*;
import com.seolstudy.seolstudy_backend.mentee.dto.*;
import com.seolstudy.seolstudy_backend.mentee.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeService {

    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FeedbackRepository feedbackRepository;
    private final TaskMaterialRepository taskMaterialRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final OverallFeedbackRepository overallFeedbackRepository;
    private final MentorMenteeRepository mentorMenteeRepository;

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
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getMenteeId().equals(menteeId)) {
            throw new RuntimeException("Access denied");
        }

        task.updateStudyTime(studyTime);
        return new UpdateStudyTimeResponse(task.getId(), task.getStudyTime());
    }

    public DailyFeedbackResponse getDailyFeedbacks(Long menteeId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);

        // 1. Get tasks for the day
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date);

        // 2. Get feedbacks for these tasks
        List<DailyFeedbackResponse.FeedbackItem> feedbackItems = new ArrayList<>();
        for (Task task : tasks) {
            Feedback feedback = feedbackRepository.findByTaskId(task.getId());
            if (feedback != null) {
                feedbackItems.add(DailyFeedbackResponse.FeedbackItem.builder()
                        .id(feedback.getId())
                        .taskId(task.getId())
                        .taskTitle(task.getTitle())
                        .subject(task.getSubject())
                        .subjectName(task.getSubject() != null ? task.getSubject().getDescription() : null)
                        .isImportant(feedback.isImportant())
                        .summary(feedback.getSummary())
                        .content(feedback.getContent())
                        .createdAt(feedback.getCreatedAt())
                        .build());
            }
        }

        // 3. Get overall feedback
        Optional<OverallFeedback> overallFeedback = overallFeedbackRepository.findByMenteeIdAndFeedbackDate(menteeId,
                date);
        String overallComment = overallFeedback.map(OverallFeedback::getContent).orElse(null);

        // 4. Get mentor name
        String mentorName = null;
        Optional<MentorMentee> mentorMentee = mentorMenteeRepository.findByMenteeId(menteeId);
        if (mentorMentee.isPresent()) {
            Optional<User> mentor = userRepository.findById(mentorMentee.get().getMentorId());
            if (mentor.isPresent()) {
                mentorName = mentor.get().getName();
            }
        }

        return DailyFeedbackResponse.builder()
                .date(date)
                .feedbacks(feedbackItems)
                .overallComment(overallComment)
                .mentorName(mentorName)
                .build();
    }

    public YesterdayFeedbackResponse getYesterdayFeedbacks(Long menteeId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1. Get tasks for yesterday
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(menteeId, yesterday);

        // 2. Get feedbacks for these tasks
        List<YesterdayFeedbackResponse.FeedbackItem> feedbackItems = new ArrayList<>();
        for (Task task : tasks) {
            Feedback feedback = feedbackRepository.findByTaskId(task.getId());
            if (feedback != null) {
                feedbackItems.add(YesterdayFeedbackResponse.FeedbackItem.builder()
                        .id(feedback.getId())
                        .taskId(task.getId())
                        .taskTitle(task.getTitle())
                        .subject(task.getSubject())
                        .subjectName(task.getSubject() != null ? task.getSubject().getDescription() : null)
                        .isImportant(feedback.isImportant())
                        .summary(feedback.getSummary())
                        .createdAt(feedback.getCreatedAt())
                        .build());
            }
        }

        // 3. Get overall feedback
        Optional<OverallFeedback> overallFeedback = overallFeedbackRepository.findByMenteeIdAndFeedbackDate(menteeId,
                yesterday);
        String overallComment = overallFeedback.map(OverallFeedback::getContent).orElse(null);

        return YesterdayFeedbackResponse.builder()
                .date(yesterday)
                .feedbacks(feedbackItems)
                .overallComment(overallComment)
                .build();
    }

}
