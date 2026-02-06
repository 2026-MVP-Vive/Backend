package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.SolutionMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionMaterialRepository extends JpaRepository<SolutionMaterial, Long> {
    List<SolutionMaterial> findAllBySolutionId(Long solutionId);

    List<SolutionMaterial> findAllBySolutionIdIn(List<Long> solutionIds);
}