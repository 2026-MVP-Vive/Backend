package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.Solution;
import com.seolstudy.seolstudy_backend.mentee.domain.SolutionMaterial;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.SolutionDto;
import com.seolstudy.seolstudy_backend.mentee.dto.SolutionMaterialDto;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionMaterialRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final SolutionMaterialRepository solutionMaterialRepository;
    private final FileRepository fileRepository;

    public List<SolutionDto> getSolutions(Long menteeId, Subject subject) {
        List<Solution> solutions;
        if (subject != null) {
            solutions = solutionRepository.findAllByMenteeIdAndSubject(menteeId, subject);
        } else {
            solutions = solutionRepository.findAllByMenteeId(menteeId);
        }

        if (solutions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> solutionIds = solutions.stream().map(Solution::getId).toList();
        List<SolutionMaterial> materials = solutionMaterialRepository.findAllBySolutionIdIn(solutionIds);

        List<Long> fileIds = materials.stream().map(SolutionMaterial::getFileId).distinct().toList();
        Map<Long, File> fileMap = fileRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(File::getId, Function.identity()));

        Map<Long, List<SolutionMaterial>> materialsBySolutionId = materials.stream()
                .collect(Collectors.groupingBy(SolutionMaterial::getSolutionId));

        return solutions.stream().map(solution -> {
            List<SolutionMaterial> solutionMaterials = materialsBySolutionId.getOrDefault(solution.getId(),
                    Collections.emptyList());
            List<SolutionMaterialDto> materialDtos = solutionMaterials.stream()
                    .map(material -> {
                        File file = fileMap.get(material.getFileId());
                        if (file == null)
                            return null;
                        return SolutionMaterialDto.builder()
                                .id(file.getId())
                                .fileName(file.getOriginalName())
                                .fileType(file.getFileType()) // Assuming fileType in File entity maps to what is
                                // needed, or extension
                                .downloadUrl("/api/v1/files/" + file.getId() + "/download")
                                .build();
                    })
                    .filter(dto -> dto != null)
                    .toList();

            return SolutionDto.builder()
                    .id(solution.getId())
                    .title(solution.getTitle())
                    .subject(solution.getSubject())
                    .subjectName(SolutionDto.getSubjectName(solution.getSubject()))
                    .materials(materialDtos)
                    .build();
        }).toList();
    }
}