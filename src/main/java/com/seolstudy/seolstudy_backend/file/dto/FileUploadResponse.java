package com.seolstudy.seolstudy_backend.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String url;
}
