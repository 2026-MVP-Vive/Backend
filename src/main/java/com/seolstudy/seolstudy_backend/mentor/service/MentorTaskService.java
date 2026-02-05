package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Feedback;
import com.seolstudy.seolstudy_backend.mentee.domain.Submission;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.dto.SubmissionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.*;
import com.seolstudy.seolstudy_backend.mentor.dto.response.FeedbackResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.GoalResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentTaskResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorTaskService {

    private final MentorMenteeRepository mentorMenteeRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FeedbackRepository feedbackRepository;

    public MentorStudentTaskResponse getStudentTasks(Long studentId, LocalDate date) {

        // 나중에 access token발급 후에 추가해야함 (테스트는 security config 테스트용으로 테스트)
//        Long mentorId = SecurityUtil.getLoginUserId();

//        // 1️⃣ 멘토-멘티 관계 검증
//        if (!mentorMenteeRepository.existsByMentorIdAndMenteeId(mentorId, studentId)) {
//            throw new IllegalArgumentException("담당 멘티가 아닙니다.");
//        }

        // 2️⃣ 멘티 정보
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // 3️⃣ 날짜별 Task 조회
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(studentId, date);

        List<TaskResponse> taskResponses = tasks.stream()
                .map(task -> {

                    Submission submission = submissionRepository.findByTaskId(task.getId());
                    Feedback feedback = feedbackRepository.findByTaskId(task.getId());

                    return new TaskResponse(
                            task.getId(),
                            task.getTitle(),
                            task.getSubject(),
                            task.getSubject() != null ? task.getSubject().name() : null,
                            task.getSolution() == null ? null :
                                    new GoalResponse(
                                            task.getSolution().getId(),
                                            task.getSolution().getTitle()
                                    ),
                            List.of(), // 🔥 TaskMaterial Repository 없으므로 비워둠
                            task.getStudyTime(),
                            task.isMentorConfirmed(),
                            submission == null ? null : SubmissionResponse.of(submission),
                            feedback == null ? null :
                                    new FeedbackResponse(
                                            feedback.getId(),
                                            feedback.isImportant()
                                    )
                    );
                })
                .toList();

        return new MentorStudentTaskResponse(
                studentId,
                student.getName(),
                date,
                taskResponses,
                List.of() // comments 없음
        );
    }
}
