package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Solution;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findAllByMenteeId(Long menteeId);

    List<Solution> findAllByMenteeIdAndSubject(Long menteeId, Subject subject);
}