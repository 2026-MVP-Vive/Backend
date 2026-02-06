package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.Solution;
import com.seolstudy.seolstudy_backend.mentee.domain.SolutionMaterial;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.SolutionDto;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionMaterialRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SolutionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

    @InjectMocks
    private SolutionService solutionService;

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private SolutionMaterialRepository solutionMaterialRepository;

    @Mock
    private FileRepository fileRepository;

    @Test
    @DisplayName("솔루션 목록을 조회한다")
    void getSolutions() {
        // given
        Long menteeId = 1L;
        Solution solution = new Solution(menteeId, "Title", Subject.KOREAN);
        ReflectionTestUtils.setField(solution, "id", 10L);

        given(solutionRepository.findAllByMenteeId(menteeId)).willReturn(List.of(solution));

        SolutionMaterial material = new SolutionMaterial(10L, 501L);
        given(solutionMaterialRepository.findAllBySolutionIdIn(anyList())).willReturn(List.of(material));

        File file = File.builder()
                .id(501L)
                .originalName("file.pdf")
                .storedName("stored.pdf")
                .filePath("/path/to/file")
                .fileType("application/pdf")
                .fileSize(100L)
                .category(File.FileCategory.MATERIAL)
                .build();
        given(fileRepository.findAllById(anyList())).willReturn(List.of(file));

        // when
        List<SolutionDto> result = solutionService.getSolutions(menteeId, null);

        // then
        assertThat(result).hasSize(1);
        SolutionDto dto = result.get(0);
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getSubject()).isEqualTo(Subject.KOREAN);
        assertThat(dto.getSubjectName()).isEqualTo("국어");
        assertThat(dto.getMaterials()).hasSize(1);
        assertThat(dto.getMaterials().get(0).getFileName()).isEqualTo("file.pdf");
    }

    @Test
    @DisplayName("과목으로 필터링하여 솔루션 목록을 조회한다")
    void getSolutionsBySubject() {
        // given
        Long menteeId = 1L;
        Subject subject = Subject.MATH;
        Solution solution = new Solution(menteeId, "Math Title", Subject.MATH);
        ReflectionTestUtils.setField(solution, "id", 11L);

        given(solutionRepository.findAllByMenteeIdAndSubject(menteeId, subject)).willReturn(List.of(solution));
        given(solutionMaterialRepository.findAllBySolutionIdIn(anyList())).willReturn(List.of());
        given(fileRepository.findAllById(anyList())).willReturn(List.of());

        // when
        List<SolutionDto> result = solutionService.getSolutions(menteeId, subject);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Math Title");
    }
}
