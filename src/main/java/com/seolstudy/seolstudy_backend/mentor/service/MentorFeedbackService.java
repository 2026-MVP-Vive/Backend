package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Feedback;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.repository.FeedbackRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorFeedbackCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorFeedbackCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorFeedbackService {

    private final TaskRepository taskRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public MentorFeedbackCreateResponse createFeedback(
            Long studentId,
            MentorFeedbackCreateRequest request
    ) {
        // 1️⃣ 필수값 검증
        if (request.getTaskId() == null) {
            throw new IllegalArgumentException("taskId는 필수입니다.");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("피드백 내용은 필수입니다.");
        }

        // 2️⃣ Task 조회
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NoSuchElementException("할 일을 찾을 수 없습니다."));

        // 3️⃣ 멘티 소유 검증
        if (!task.getMenteeId().equals(studentId)) {
            throw new IllegalArgumentException("해당 멘티의 할 일이 아닙니다.");
        }

        // 4️⃣ 이미 피드백 존재 여부
        if (feedbackRepository.existsByTaskId(task.getId())) {
            throw new IllegalArgumentException("이미 피드백이 작성된 할 일입니다.");
        }

        // 5️⃣ Feedback 생성
        Feedback feedback = new Feedback();
        feedback.setTaskId(task.getId());
        feedback.setMentorId(studentId); // ⚠️ JWT 붙이면 mentorId로 교체
        feedback.setContent(request.getContent());
        feedback.setSummary(request.getSummary());
        feedback.setImportant(Boolean.TRUE.equals(request.getIsImportant()));

        Feedback saved = feedbackRepository.save(feedback);

        return new MentorFeedbackCreateResponse(
                saved.getId(),
                saved.getTaskId(),
                saved.getContent(),
                saved.getSummary(),
                saved.isImportant(),
                saved.getCreatedAt()
        );
    }
}
