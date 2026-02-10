package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.dto.FileUploadResponse;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.global.fcm.service.FcmService;
import com.seolstudy.seolstudy_backend.mentee.domain.Submission;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.SubmissionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SubmissionService {

    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FileService fileService;
    private final FcmService fcmService;

    @Transactional
    public SubmissionResponse submitTask(Long menteeId, Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getMenteeId().equals(menteeId)) {
            throw new RuntimeException("Access denied");
        }

        if (submissionRepository.existsByTaskId(taskId)) {
            throw new RuntimeException("Submission already exists");
        }

        try {
            //로컬 파일에 저장하는 코드, 로컬에서 테스트 시 FileService에 있는 saveFile 주석풀고 밑에 주석 해제한 후 테스트
            //            File savedFile = fileService.saveFile(file, File.FileCategory.SUBMISSION, menteeId);

            /** s3 파일 저장 코드
             *  FileService에서 SecurityUtil 호출하여 회원 ID를 전달하므로 파라미터로 전달할 필요 X
             * */
            FileUploadResponse fileUploadResponse = fileService.uploadFile(file, File.FileCategory.SUBMISSION);

            Submission submission = new Submission(taskId, fileUploadResponse.getId());
            submissionRepository.save(submission);

            /** Fcm 로직 추가 */
            sendNotificationToMentor(menteeId, taskId);

            return SubmissionResponse.of(submission);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Transactional
    public void sendNotificationToMentor(Long menteeId, Long taskId){
        List<FcmToken> fcmToken = fcmTokenRepository.findAllByUserId(menteeId);
        if (fcmToken.isEmpty()) {
            log.warn("멘티(ID: {})의 FCM 토큰이 없어 알림을 보낼 수 없습니다.", menteeId);
            return;
        }
        for(FcmToken token : fcmToken){
            fcmService.sendNotification(token.getToken(), "과제 제출 알림", "멘티가 과제를 제출했습니다! 확인해 주세요.", taskId);
        }
    }
}