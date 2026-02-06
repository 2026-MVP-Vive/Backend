package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.PlannerCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PlannerCompletionRepository extends JpaRepository<PlannerCompletion, Long> {
    Optional<PlannerCompletion> findByMenteeIdAndPlanDate(Long menteeId, LocalDate planDate);

    boolean existsByMenteeIdAndPlanDate(Long menteeId, LocalDate planDate);
}
