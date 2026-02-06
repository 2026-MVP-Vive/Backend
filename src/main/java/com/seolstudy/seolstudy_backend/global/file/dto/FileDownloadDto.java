package com.seolstudy.seolstudy_backend.global.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FileDownloadDto {
    private String originalName;
    private String fileType;
    private long fileSize;
    private Resource resource;
}
