package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnAttachmentRepository extends JpaRepository<ColumnAttachment, Long> {
    List<ColumnAttachment> findAllByColumnId(Long columnId);
}
