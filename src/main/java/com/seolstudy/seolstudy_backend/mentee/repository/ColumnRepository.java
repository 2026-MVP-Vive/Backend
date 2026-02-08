package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRepository extends JpaRepository<ColumnEntity, Long> {
    Page<ColumnEntity> findAll(Pageable pageable);
}
