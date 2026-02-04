package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.OverallFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface OverallFeedbackRepository extends JpaRepository<OverallFeedback, Long> {
    Optional<OverallFeedback> findByMenteeIdAndFeedbackDate(Long menteeId, LocalDate feedbackDate);
}
