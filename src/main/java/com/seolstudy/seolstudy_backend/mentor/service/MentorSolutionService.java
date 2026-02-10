package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.dto.FileUploadResponse;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.mentee.domain.Solution;
import com.seolstudy.seolstudy_backend.mentee.domain.SolutionMaterial;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionMaterialRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentor.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorSolutionService {

    private final UserRepository userRepository;
    private final SolutionRepository solutionRepository;
    private final SolutionMaterialRepository solutionMaterialRepository;
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;
    private final FileService fileService;

    public MentorStudentSolutionResponse getStudentSolutions(
            Long studentId,
            String subject
    ) {
        // 1️⃣ 멘티 확인
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // 2️⃣ 솔루션 조회
        List<Solution> solutions = (subject == null)
                ? solutionRepository.findAllByMenteeId(studentId)
                : solutionRepository.findAllByMenteeIdAndSubject(
                studentId,
                Subject.valueOf(subject)
        );

        if (solutions.isEmpty()) {
            return new MentorStudentSolutionResponse(
                    studentId,
                    student.getName(),
                    List.of()
            );
        }

        // 3️⃣ Solution ID 목록
        List<Long> solutionIds = solutions.stream()
                .map(Solution::getId)
                .toList();

        // 4️⃣ SolutionMaterial 조회
        List<SolutionMaterial> materials =
                solutionMaterialRepository.findAllBySolutionIdIn(solutionIds);

        // 5️⃣ File 조회 (Map으로 묶기)
        List<Long> fileIds = materials.stream()
                .map(SolutionMaterial::getFileId)
                .toList();

        var fileMap = fileRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(File::getId, f -> f));

        // 6️⃣ Solution별 Material 묶기
        Map<Long, List<MaterialResponse>> materialMap = new HashMap<>();

        for (SolutionMaterial sm : materials) {
            File file = fileMap.get(sm.getFileId());
            materialMap.computeIfAbsent(sm.getSolutionId(), k -> new ArrayList<>())
                    .add(new MaterialResponse(
                            file.getId(),
                            file.getOriginalName(),
                            "/api/v1/files/" + file.getId() + "/download"
                    ));
        }

        // 7️⃣ Response 변환
        List<SolutionItemResponse> responseList = solutions.stream()
                .map(sol -> new SolutionItemResponse(
                        sol.getId(),
                        sol.getTitle(),
                        sol.getSubject().name(),
                        sol.getSubject().getDescription(), // Subject enum에 메서드 있어야 함
                        materialMap.getOrDefault(sol.getId(), List.of()),
                        taskRepository.countBySolutionId(sol.getId()),
                        sol.getCreatedAt().toLocalDate()
                ))
                .toList();

        return new MentorStudentSolutionResponse(
                studentId,
                student.getName(),
                responseList
        );
    }

    @Transactional
    public MentorSolutionCreateResponse createSolution(
            Long studentId,
            String title,
            Subject subject,
            List<MultipartFile> materials
    ) {

        // 1️⃣ 멘티 존재 확인
        userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("멘티를 찾을 수 없습니다."));

        // 2️⃣ Solution 생성
        Solution solution = new Solution(studentId, title, subject);
        solutionRepository.save(solution);

        // 3️⃣ 파일 업로드 + SolutionMaterial 연결
        List<MaterialResponse> materialResponses = List.of();

        if (materials != null && !materials.isEmpty()) {
            materialResponses = materials.stream()
                    .map(file -> {
                        FileUploadResponse upload =
                                fileService.uploadFile(
                                        file,
                                        File.FileCategory.MATERIAL,
                                        studentId
                                );

                        solutionMaterialRepository.save(
                                new SolutionMaterial(
                                        solution.getId(),
                                        upload.getId()
                                )
                        );

                        return new MaterialResponse(
                                upload.getId(),
                                upload.getFileName(),
                                upload.getUrl() + "/download"
                        );
                    })
                    .toList();
        }

        // 4️⃣ 응답
        return new MentorSolutionCreateResponse(
                solution.getId(),
                solution.getTitle(),
                solution.getSubject(),
                solution.getSubject().name(), // or getDisplayName() 있으면 그걸로
                materialResponses,
                solution.getCreatedAt()
        );
    }

    @Transactional
    public MentorSolutionUpdateResponse updateSolution(
            Long studentId,
            Long solutionId,
            String title,
            Subject subject,
            List<MultipartFile> materials,
            List<Long> deleteFileIds
    ) {

        // 1️⃣ Solution 조회
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new NoSuchElementException("솔루션을 찾을 수 없습니다."));

        // 2️⃣ 멘티 검증
        if (!solution.getMenteeId().equals(studentId)) {
            throw new IllegalArgumentException("해당 멘티의 솔루션이 아닙니다.");
        }

        // 3️⃣ 제목 수정
        if (title != null && !title.isBlank()) {
            solution.setTitle(title);
        }

        // 4️⃣ 과목 수정
        if (subject != null) {
            solution.setSubject(subject);
        }

        // 5️⃣ 기존 파일 삭제
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            deleteFileIds.forEach(fileId -> {

                // solution_materials 삭제
                solutionMaterialRepository
                        .findAllBySolutionId(solutionId)
                        .stream()
                        .filter(sm -> sm.getFileId().equals(fileId))
                        .findFirst()
                        .ifPresent(solutionMaterialRepository::delete);

                // 실제 파일 삭제 (S3 + DB)
                fileService.deleteFile(fileId);
            });
        }

        // 6️⃣ 새 파일 추가
        if (materials != null && !materials.isEmpty()) {
            materials.forEach(file -> {
                FileUploadResponse upload =
                        fileService.uploadFile(
                                file,
                                File.FileCategory.MATERIAL,
                                studentId
                        );

                solutionMaterialRepository.save(
                        new SolutionMaterial(
                                solutionId,
                                upload.getId()
                        )
                );
            });
        }

        // 7️⃣ 저장
        solutionRepository.save(solution);

        return new MentorSolutionUpdateResponse(
                solution.getId(),
                solution.getTitle(),
                solution.getSubject(),
                solution.getUpdatedAt()
        );
    }
    @Transactional
    public void deleteSolution(Long studentId, Long solutionId) {

        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new NoSuchElementException("솔루션을 찾을 수 없습니다."));

        // (JWT 붙기 전) 멘티 소유 검증
        if (!solution.getMenteeId().equals(studentId)) {
            throw new IllegalArgumentException("해당 멘티의 솔루션이 아닙니다.");
        }

        // 🔥 핵심 로직
        long linkedTaskCount = taskRepository.countBySolutionId(solutionId);
        if (linkedTaskCount > 0) {
            throw new IllegalArgumentException("연결된 할 일이 있어 삭제할 수 없습니다.");
        }

        // 연결된 파일 정리
        List<SolutionMaterial> materials =
                solutionMaterialRepository.findAllBySolutionId(solutionId);
        solutionMaterialRepository.deleteAll(materials);

        solutionRepository.delete(solution);
    }


}