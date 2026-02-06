package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.mentee.domain.MentorMentee;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.repository.FeedbackRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorStudentService {

    private final MentorMenteeRepository mentorMenteeRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final FeedbackRepository feedbackRepository;

    public MentorStudentResponse getMyStudents() {
        Long mentorId = getCurrentMentorId(); // 🔑 JWT에서 꺼낸다고 가정
        LocalDate today = LocalDate.now();

        List<MentorMentee> relations =
                mentorMenteeRepository.findAllByMentorId(mentorId);

        List<MentorStudentResponse.StudentInfo> students =
                relations.stream()
                        .map(r -> buildStudentInfo(r.getMenteeId(), today))
                        .toList();

        return MentorStudentResponse.builder()
                .students(students)
                .build();
    }

    private MentorStudentResponse.StudentInfo buildStudentInfo(Long menteeId, LocalDate today) {
        User mentee = userRepository.findById(menteeId).orElseThrow();

        List<Task> todayTasks =
                taskRepository.findAllByMenteeIdAndTaskDate(menteeId, today);

        int total = todayTasks.size();
        int completed = (int) todayTasks.stream()
                .filter(t -> t.getStudyTime() != null)
                .count();

        int pendingConfirmation = (int) todayTasks.stream()
                .filter(t -> t.getStudyTime() != null && !t.isMentorConfirmed())
                .count();

        boolean hasPendingFeedback =
                todayTasks.stream()
                        .anyMatch(t ->
                                t.getStudyTime() != null &&
                                        !feedbackRepository.existsByTaskId(t.getId())
                        );

        LocalDate lastFeedbackDate =
                todayTasks.stream()
                        .map(Task::getId)
                        .flatMap(taskId ->
                                feedbackRepository.findAllByTaskIdIn(List.of(taskId))
                                        .stream()
                        )
                        .map(f -> f.getCreatedAt().toLocalDate())
                        .max(LocalDate::compareTo)
                        .orElse(null);

        return MentorStudentResponse.StudentInfo.builder()
                .id(mentee.getId())
                .name(mentee.getName())
                .profileImageUrl("/api/v1/files/profile/" + mentee.getId())
                .todayTaskSummary(
                        MentorStudentResponse.TodayTaskSummary.builder()
                                .total(total)
                                .completed(completed)
                                .pendingConfirmation(pendingConfirmation)
                                .build()
                )
                .lastFeedbackDate(lastFeedbackDate)
                .hasPendingFeedback(hasPendingFeedback)
                .build();
    }

    private Long getCurrentMentorId() {
        // TODO: SecurityContextHolder에서 userId 추출
        return 1L; // 임시
    }
}