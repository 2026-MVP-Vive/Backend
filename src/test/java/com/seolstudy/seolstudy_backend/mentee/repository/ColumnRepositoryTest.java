package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("local")
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ColumnRepositoryTest {

    @Autowired
    private ColumnRepository columnRepository;

    @Test
    @DisplayName("칼럼 목록을 페이징하여 조회한다")
    void findAll() {
        // given
        ColumnEntity column1 = new ColumnEntity();
        ReflectionTestUtils.setField(column1, "title", "Title 1");
        ReflectionTestUtils.setField(column1, "content", "Content 1");
        ReflectionTestUtils.setField(column1, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(column1, "updatedAt", LocalDateTime.now());

        ColumnEntity column2 = new ColumnEntity();
        ReflectionTestUtils.setField(column2, "title", "Title 2");
        ReflectionTestUtils.setField(column2, "content", "Content 2");
        ReflectionTestUtils.setField(column2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(column2, "updatedAt", LocalDateTime.now());

        columnRepository.saveAll(List.of(column1, column2));

        // when
        Page<ColumnEntity> result = columnRepository.findAll(PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("title")
                .containsExactly("Title 1", "Title 2");
    }
}
