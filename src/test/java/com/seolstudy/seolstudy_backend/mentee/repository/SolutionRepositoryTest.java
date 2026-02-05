package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Solution;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("local")
class SolutionRepositoryTest {

    @Autowired
    private SolutionRepository solutionRepository;

    @Test
    @DisplayName("멘티 ID로 솔루션 목록을 조회한다")
    void findAllByMenteeId() {
        // given
        Long menteeId = 1L;
        Solution solution1 = new Solution(menteeId, "Title 1", Subject.KOREAN);
        Solution solution2 = new Solution(menteeId, "Title 2", Subject.MATH);
        solutionRepository.saveAll(List.of(solution1, solution2));

        // when
        List<Solution> result = solutionRepository.findAllByMenteeId(menteeId);

        // then
        assertThat(result).hasSize(2)
                .extracting("title")
                .containsExactlyInAnyOrder("Title 1", "Title 2");
    }

    @Test
    @DisplayName("멘티 ID와 과목으로 솔루션 목록을 조회한다")
    void findAllByMenteeIdAndSubject() {
        // given
        Long menteeId = 1L;
        Solution solution1 = new Solution(menteeId, "Title 1", Subject.KOREAN);
        Solution solution2 = new Solution(menteeId, "Title 2", Subject.MATH);
        solutionRepository.saveAll(List.of(solution1, solution2));

        // when
        List<Solution> result = solutionRepository.findAllByMenteeIdAndSubject(menteeId, Subject.KOREAN);

        // then
        assertThat(result).hasSize(1)
                .extracting("title")
                .containsExactly("Title 1");
    }
}
