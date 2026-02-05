package com.seolstudy.seolstudy_backend.file.repository;

import com.seolstudy.seolstudy_backend.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findById(Long id);
}
