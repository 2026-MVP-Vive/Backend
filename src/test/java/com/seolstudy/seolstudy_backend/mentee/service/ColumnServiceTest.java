package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnListResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ColumnServiceTest {

    @InjectMocks
    private ColumnService columnService;

    @Mock
    private ColumnRepository columnRepository;

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
}
