package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.ColumnAttachment;
import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnListResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.ColumnAttachmentRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.ColumnRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ColumnServiceTest {

    @InjectMocks
    private ColumnService columnService;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private ColumnAttachmentRepository columnAttachmentRepository;

    @Mock
    private FileRepository fileRepository;

    @Test
    @DisplayName("칼럼 목록을 조회한다")
    void getColumns() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        ColumnEntity column = new ColumnEntity();
        ReflectionTestUtils.setField(column, "id", 1L);
        ReflectionTestUtils.setField(column, "title", "Title");
        ReflectionTestUtils.setField(column, "createdAt", LocalDateTime.now());

        Page<ColumnEntity> page = new PageImpl<>(List.of(column), pageable, 1);
        given(columnRepository.findAll(pageable)).willReturn(page);

        // when
        ColumnListResponse response = columnService.getColumns(pageable);

        // then
        assertThat(response.getColumns()).hasSize(1);
        assertThat(response.getColumns().get(0).getTitle()).isEqualTo("Title");
        assertThat(response.getPagination().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("칼럼 상세 정보를 조회한다")
    void getColumnDetail() {
        // given
        Long columnId = 1L;
        ColumnEntity column = new ColumnEntity();
        ReflectionTestUtils.setField(column, "id", columnId);
        ReflectionTestUtils.setField(column, "title", "Detail Title");
        ReflectionTestUtils.setField(column, "content", "Detail Content");
        ReflectionTestUtils.setField(column, "createdAt", LocalDateTime.now());

        given(columnRepository.findById(columnId)).willReturn(Optional.of(column));

        ColumnAttachment attachment = new ColumnAttachment();
        ReflectionTestUtils.setField(attachment, "fileId", 100L);
        given(columnAttachmentRepository.findAllByColumnId(columnId)).willReturn(List.of(attachment));

        File file = File.builder()
                .id(100L)
                .originalName("test.pdf")
                .build();
        given(fileRepository.findAllById(List.of(100L))).willReturn(List.of(file));

        // when
        ColumnDetailResponse response = columnService.getColumnDetail(columnId);

        // then
        assertThat(response.getId()).isEqualTo(columnId);
        assertThat(response.getTitle()).isEqualTo("Detail Title");
        assertThat(response.getAttachments()).hasSize(1);
        assertThat(response.getAttachments().get(0).getFileName()).isEqualTo("test.pdf");
    }

    @Test
    @DisplayName("존재하지 않는 칼럼 조회 시 예외가 발생한다")
    void getColumnDetail_NotFound() {
        // given
        Long columnId = 1L;
        given(columnRepository.findById(columnId)).willReturn(Optional.empty());

        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> columnService.getColumnDetail(columnId))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .hasMessageContaining("Column not found with id: " + columnId);
    }
}
