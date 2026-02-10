package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.global.fcm.controller.FcmTokenController;
import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.global.fcm.service.FcmService;
import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.dto.FileUploadResponse;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.mentee.domain.*;
import com.seolstudy.seolstudy_backend.mentee.dto.SubmissionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.*;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorTaskConfirmRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorTaskCreateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.request.MentorTaskUpdateRequest;
import com.seolstudy.seolstudy_backend.mentor.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MentorTaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FeedbackRepository feedbackRepository;
    private final SolutionRepository solutionRepository;
    private final TaskMaterialRepository taskMaterialRepository;
    private final FileService fileService;
    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;

    public MentorStudentTaskResponse getStudentTasks(Long studentId, LocalDate date) {

        // 나중에 access token발급 후에 추가해야함 (테스트는 security config 테스트용으로 테스트)
        // Long mentorId = SecurityUtil.getLoginUserId();

        // // 1️⃣ 멘토-멘티 관계 검증
        // if (!mentorMenteeRepository.existsByMentorIdAndMenteeId(mentorId, studentId))
        // {
        // throw new IllegalArgumentException("담당 멘티가 아닙니다.");
        // }

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
                            task.getSolution() == null ? null
                                    : new GoalResponse(
                                    task.getSolution().getId(),
                                    task.getSolution().getTitle()),
                            List.of(), // 🔥 TaskMaterial Repository 없으므로 비워둠
                            task.getStudyTime(),
                            task.isUploadRequired(),
                            task.isMentorConfirmed(),
                            submission == null ? null : SubmissionResponse.of(submission),
                            feedback == null ? null
                                    : new FeedbackResponse(
                                    feedback.getId(),
                                    feedback.isImportant()));
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

    @Transactional
    public MentorTaskCreateResponse createStudentTask(
            Long studentId,
            MentorTaskCreateRequest request) {
        // 1️⃣ 멘티 확인
        userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // 2️⃣ Solution 조회 (선택)
        Solution solution = null;
        if (request.getGoalId() != null) {
            solution = solutionRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new NoSuchElementException("목표를 찾을 수 없습니다."));
        }

        // 3️⃣ Task 생성
        Task task = new Task(
                studentId,
                request.getTitle(),
                request.getDate(),
                null,
                studentId // ⚠️ 임시 (JWT 붙이면 mentorId로 교체)
        );
        // 멘토가 준 할 일이므로
        task.setMentorAssigned(true);
        task.setMentorConfirmed(false);
        task.setUploadRequired(true); // Mentor assigned tasks require upload by default

        // 목표(솔루션) 연결
        if (solution != null) {
            task.setSolution(solution);
            task.setSubject(solution.getSubject());
        }

        taskRepository.save(task);

        // 4️⃣ TaskMaterial 연결
        List<MaterialResponse> materials = List.of();
        if (request.getMaterialIds() != null && !request.getMaterialIds().isEmpty()) {
            materials = request.getMaterialIds().stream()
                    .map(fileId -> {
                        taskMaterialRepository.save(
                                new TaskMaterial(task.getId(), fileId));
                        return new MaterialResponse(
                                fileId,
                                null,
                                "/api/v1/files/" + fileId + "/download");
                    })
                    .toList();
        }

        // 5️⃣ 응답
        return new MentorTaskCreateResponse(
                task.getId(),
                task.getTitle(),
                task.getCreatedAt(),
                task.getSubject(),
                solution == null ? null : new GoalResponse(solution.getId(), solution.getTitle()),
                materials);
    }

    @Transactional
    public MentorTaskCreateResponse createStudentTaskMultipart(
            Long studentId,
            String title,
            LocalDate date,
            Long goalId,
            List<MultipartFile> materials) {

        Solution solution = goalId == null ? null
                : solutionRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("목표 없음"));

        Task task = new Task(studentId, title, date, null, studentId);
        task.setMentorAssigned(true);
        task.setUploadRequired(true);

        if (solution != null) {
            task.setSolution(solution);
            task.setSubject(solution.getSubject());
        }

        taskRepository.save(task);

        List<MaterialResponse> materialResponses = List.of();

        if (materials != null && !materials.isEmpty()) {
            materialResponses = materials.stream()
                    .map(file -> {
                        try {
                            FileUploadResponse saved = fileService.uploadFile(
                                    file,
                                    File.FileCategory.MATERIAL,
                                    studentId);

                            taskMaterialRepository.save(
                                    new TaskMaterial(task.getId(), saved.getId()));

                            return new MaterialResponse(
                                    saved.getId(),
                                    saved.getFileName(),
                                    "/api/v1/files/" + saved.getId() + "/download");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }

        return new MentorTaskCreateResponse(
                task.getId(),
                task.getTitle(),
                task.getCreatedAt(),
                task.getSubject(),
                solution == null ? null : new GoalResponse(solution.getId(), solution.getTitle()),
                materialResponses);
    }

    @Transactional
    public MentorTaskUpdateResponse updateStudentTask(
            Long studentId,
            Long taskId,
            MentorTaskUpdateRequest request) {
        // 1️⃣ 멘티 확인
        userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // 2️⃣ Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("할 일을 찾을 수 없습니다."));

        // 3️⃣ 멘티 소유 Task인지 검증
        if (!task.getMenteeId().equals(studentId)) {
            throw new IllegalArgumentException("해당 멘티의 할 일이 아닙니다.");
        }

        // 4️⃣ title 수정
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        // 5️⃣ date 수정
        if (request.getDate() != null) {
            task.setTaskDate(request.getDate());
        }

        // 6️⃣ goalId 수정
        if (request.getGoalId() != null) {
            Solution solution = solutionRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new NoSuchElementException("목표를 찾을 수 없습니다."));

            task.setSolution(solution);
            task.setSubject(solution.getSubject());
        }

        taskRepository.save(task);

        // 7️⃣ 응답
        return new MentorTaskUpdateResponse(
                task.getId(),
                task.getTitle(),
                task.getTaskDate(),
                task.getUpdatedAt());
    }

    @Transactional
    public void deleteStudentTask(Long studentId, Long taskId) {

        // 1️⃣ Task 존재 확인
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("할 일을 찾을 수 없습니다."));

        // 2️⃣ 해당 멘티의 할 일인지 검증
        if (!task.getMenteeId().equals(studentId)) {
            throw new IllegalArgumentException("해당 멘티의 할 일이 아닙니다.");
        }

        // 3️⃣ 연관 데이터 먼저 삭제 (명시적으로)
        taskMaterialRepository.deleteByTaskId(taskId);
        submissionRepository.deleteByTaskId(taskId);
        feedbackRepository.deleteByTaskId(taskId);

        // 4️⃣ Task 삭제
        taskRepository.delete(task);
    }

    @Transactional
    public MentorTaskConfirmResponse confirmTask(
            Long studentId,
            Long taskId,
            MentorTaskConfirmRequest request) {

        if (request.getConfirmed() == null) {
            throw new IllegalArgumentException("confirmed 값은 필수입니다.");
        }

        // 1️⃣ Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("할 일을 찾을 수 없습니다."));

        // 2️⃣ 멘티 소유 검증
        if (!task.getMenteeId().equals(studentId)) {
            throw new IllegalArgumentException("해당 멘티의 할 일이 아닙니다.");
        }

        // 3️⃣ 상태 변경
        if (request.getConfirmed()) {
            task.setMentorConfirmed(true);
            task.setConfirmedAt(LocalDateTime.now());

            // 🚀 FCM 알림 전송 로직 추가
            try {
                Long menteeId = task.getMenteeId();
                List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(menteeId);

                if (tokens != null && !tokens.isEmpty()) {
                    for (FcmToken token : tokens) {
                        fcmService.sendNotification(
                                token.getToken(),
                                "✅ 과제 확인 완료",
                                "멘토님이 '" + task.getTitle() + "' 과제를 확인하셨어요! 고생 많으셨습니다. 😊",
                                task.getId()
                        );
                    }
                    log.info("멘티(ID: {})에게 과제 컨펌 알림 전송 완료", menteeId);
                } else {
                    log.warn("멘티(ID: {})의 FCM 토큰이 없어 알림을 전송하지 못했습니다.", menteeId);
                }
            } catch (Exception e) {
                // 알림 실패가 DB 업데이트(Transaction)에 영향을 주지 않도록 예외 격리
                log.error("과제 컨펌 알림 전송 중 오류 발생: {}", e.getMessage());
            }

        } else {
            // 컨펌 취소 시에는 보통 알림을 보내지 않거나, 필요에 따라 별도 메시지 구성 가능
            task.setMentorConfirmed(false);
            task.setConfirmedAt(null);
        }

        return new MentorTaskConfirmResponse(
                task.getId(),
                task.isMentorConfirmed(),
                task.getConfirmedAt());
    }

}