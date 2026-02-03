package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.TaskMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskMaterialRepository extends JpaRepository<TaskMaterial, Long> {
    int countByTaskId(Long taskId);
}
