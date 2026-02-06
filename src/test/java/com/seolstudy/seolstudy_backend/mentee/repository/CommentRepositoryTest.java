package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.global.config.JpaConfig;
import com.seolstudy.seolstudy_backend.mentee.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("코멘트를 저장하고 조회한다")
    void saveAndFindComment() {
        // given
        Long menteeId = 1L;
        Comment comment = Comment.builder()
                .menteeId(menteeId)
                .content("질문이 있어요")
                .commentDate(LocalDate.now())
                .build();

        // when
        Comment savedComment = commentRepository.save(comment);

        // then
        Comment foundComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(foundComment.getContent()).isEqualTo("질문이 있어요");
        assertThat(foundComment.getMenteeId()).isEqualTo(menteeId);
        assertThat(foundComment.getCreatedAt()).isNotNull();
    }
}
