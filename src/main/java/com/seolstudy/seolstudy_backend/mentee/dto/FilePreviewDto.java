package com.seolstudy.seolstudy_backend.file.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

/** 파일을 inline(미리보기)로 전송 시 Dto */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FilePreviewDto {
    private String originalName;
    private String fileType;
    private Resource resource;
}
