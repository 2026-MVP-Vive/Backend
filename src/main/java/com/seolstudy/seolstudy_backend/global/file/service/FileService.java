package com.seolstudy.seolstudy_backend.global.file.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public File saveFile(MultipartFile multipartFile, File.FileCategory category, Long uploaderId) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String originalName = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String storedName = UUID.randomUUID().toString() + extension;
        String uploadDir = "uploads";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(storedName);
        multipartFile.transferTo(filePath.toFile());

        File fileEntity = File.builder()
                .originalName(originalName)
                .storedName(storedName)
                .filePath(filePath.toString())
                .fileType(multipartFile.getContentType())
                .fileSize(multipartFile.getSize())
                .category(category)
                .uploaderId(uploaderId)
                .build();

        return fileRepository.save(fileEntity);
    }
}
