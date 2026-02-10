package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);

    Submission findByTaskId(Long taskId);

    List<Submission> findAllByTaskIdIn(List<Long> taskIds);
}