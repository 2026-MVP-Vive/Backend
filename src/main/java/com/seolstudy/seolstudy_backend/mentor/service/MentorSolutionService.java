package com.seolstudy.seolstudy_backend.mentor.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.Solution;
import com.seolstudy.seolstudy_backend.mentee.domain.SolutionMaterial;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionMaterialRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MaterialResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.MentorStudentSolutionResponse;
import com.seolstudy.seolstudy_backend.mentor.dto.response.SolutionItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
