package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.global.fcm.service.FcmService;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.Feedback;
import com.seolstudy.seolstudy_backend.mentee.domain.OverallFeedback;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.repository.FeedbackRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.OverallFeedbackRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorFeedbackCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorFeedbackUpdateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorOverallFeedbackRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorFeedbackCreateResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorFeedbackUpdateResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorOverallFeedbackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MentorFeedbackService {

    private final TaskRepository taskRepository;
    private final FeedbackRepository feedbackRepository;

    private final UserRepository userRepository;
    private final OverallFeedbackRepository overallFeedbackRepository;

    private final FcmService fcmService; //알람 전송을 위한 서비스
    private final FcmTokenRepository fcmTokenRepository;

    private final SecurityUtil securityUtil;

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

        // 4️⃣ 이미 피드백 존재 여부
        if (feedbackRepository.existsByTaskId(task.getId())) {
            throw new IllegalArgumentException("이미 피드백이 작성된 할 일입니다.");
        }

        // 5️⃣ Feedback 생성
        Feedback feedback = new Feedback();
        feedback.setTaskId(task.getId());
        feedback.setMentorId(securityUtil.getCurrentUserId());
        feedback.setContent(request.getContent());
        feedback.setSummary(request.getSummary());
        feedback.setImportant(Boolean.TRUE.equals(request.getIsImportant()));

        Feedback saved = feedbackRepository.save(feedback);

        try {
            Long menteeId = task.getMenteeId();
            List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(menteeId);

            // 토큰이 없어도 에러를 던지지 않고 로그만 남기고 넘어갑니다.
            if (tokens != null && !tokens.isEmpty()) {
                for (FcmToken token : tokens) {
                    fcmService.sendNotification(
                            token.getToken(),
                            "✅ 피드백 등록 완료",
                            "멘토님으로부터 새로운 피드백이 도착했어요! 지금 바로 확인해 보세요. 📝",
                            task.getId()
                    );
                }
                log.info("멘티(ID: {})에게 피드백 알림 전송 완료", menteeId);
            } else {
                log.warn("멘티(ID: {})의 FCM 토큰이 없어 알림을 전송하지 못했습니다.", menteeId);
            }
        } catch (Exception e) {
            // 알림 전송 중 에러가 나더라도 피드백 저장은 유지되도록 로그만 찍습니다.
            log.error("피드백 알림 전송 중 알 수 없는 오류 발생: {}", e.getMessage());
        }

        return new MentorFeedbackCreateResponse(
                saved.getId(),
                saved.getTaskId(),
                saved.getContent(),
                saved.getSummary(),
                saved.isImportant(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public MentorFeedbackUpdateResponse updateFeedback(
            Long feedbackId,
            MentorFeedbackUpdateRequest request
    ) {
        // 1️⃣ Feedback 조회
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NoSuchElementException("피드백을 찾을 수 없습니다."));

        // 2️⃣ 수정할 값이 하나도 없는 경우
        if (request.getContent() == null
                && request.getSummary() == null
                && request.getIsImportant() == null) {
            throw new IllegalArgumentException("수정할 값이 없습니다.");
        }

        // 3️⃣ 필드별 부분 수정
        if (request.getContent() != null) {
            feedback.setContent(request.getContent());
        }

        if (request.getSummary() != null) {
            feedback.setSummary(request.getSummary());
        }

        if (request.getIsImportant() != null) {
            feedback.setImportant(request.getIsImportant());
        }

        // save() 필요 없음 (dirty checking)

        return new MentorFeedbackUpdateResponse(
                feedback.getId(),
                feedback.getContent(),
                feedback.getSummary(),
                feedback.isImportant(),
                feedback.getUpdatedAt()
        );
    }
    @Transactional
    public MentorOverallFeedbackResponse upsertOverallFeedback(
            Long studentId,
            MentorOverallFeedbackRequest request
    ) {
        // 1️⃣ 검증
        if (request.getDate() == null) {
            throw new IllegalArgumentException("date는 필수입니다.");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("총평 내용은 필수입니다.");
        }

        // 2️⃣ 멘티 존재 확인
        userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // ⚠️ JWT 붙이기 전 임시 mentorId
        Long mentorId = studentId;

        // 3️⃣ 기존 총평 조회
        OverallFeedback overallFeedback =
                overallFeedbackRepository
                        .findByMenteeIdAndFeedbackDate(studentId, request.getDate())
                        .orElseGet(() ->
                                new OverallFeedback(
                                        studentId,
                                        mentorId,
                                        request.getDate(),
                                        request.getContent()
                                )
                        );

        // 4️⃣ 기존 데이터면 수정
        overallFeedback.updateContent(request.getContent());

        OverallFeedback saved = overallFeedbackRepository.save(overallFeedback);

        return new MentorOverallFeedbackResponse(
                saved.getId(),
                saved.getFeedbackDate(),
                saved.getContent(),
                saved.getUpdatedAt()
        );
    }
}