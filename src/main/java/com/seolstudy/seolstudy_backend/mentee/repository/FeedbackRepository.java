package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByTaskId(Long taskId);

    Feedback findByTaskId(Long taskId);
    void deleteByTaskId(Long taskId);

    // 멘티의 모든 피드백 조회 (최근 날짜 계산용)
    List<Feedback> findAllByTaskIdIn(List<Long> taskIds);
}