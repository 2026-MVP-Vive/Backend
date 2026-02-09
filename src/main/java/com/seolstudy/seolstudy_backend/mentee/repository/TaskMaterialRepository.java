package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.TaskMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskMaterialRepository extends JpaRepository<TaskMaterial, Long> {
    int countByTaskId(Long taskId);

    List<TaskMaterial> findAllByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);

    List<TaskMaterial> findAllByTaskIdIn(List<Long> taskIds);
}
