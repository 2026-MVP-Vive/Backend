package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.mentee.domain.Submission;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.SubmissionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final FileService fileService;

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

        Long fileId = null;
        if (file != null && !file.isEmpty()) {
            try {
                File savedFile = fileService.saveFile(file, File.FileCategory.SUBMISSION, menteeId);
                fileId = savedFile.getId();
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file", e);
            }
        }

        Submission submission = new Submission(taskId, fileId);
        submissionRepository.save(submission);

        return SubmissionResponse.of(submission);
    }
}
