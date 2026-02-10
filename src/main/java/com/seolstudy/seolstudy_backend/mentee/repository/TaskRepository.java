package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByMenteeIdAndTaskDate(Long menteeId, LocalDate taskDate);

    List<Task> findAllByMenteeIdAndTaskDateBetween(Long menteeId, LocalDate startDate, LocalDate endDate);
    long countBySolutionId(Long solutionId);
    @Query("SELECT COUNT(DISTINCT t.taskDate) FROM Task t WHERE t.menteeId = :menteeId")
    long countDistinctTaskDateByMenteeId(Long menteeId);

    List<Task> findAllByTaskDateAndIsMentorConfirmedFalse(LocalDate taskDate);
}