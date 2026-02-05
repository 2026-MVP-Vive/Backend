package com.seolstudy.seolstudy_backend.global.file.repository;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
