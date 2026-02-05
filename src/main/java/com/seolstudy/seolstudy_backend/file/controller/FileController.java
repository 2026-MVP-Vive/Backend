package com.seolstudy.seolstudy_backend.file.controller;

import com.seolstudy.seolstudy_backend.file.domain.File;
import com.seolstudy.seolstudy_backend.file.dto.FileDownloadDto;
import com.seolstudy.seolstudy_backend.file.dto.FileUploadResponse;
import com.seolstudy.seolstudy_backend.file.service.FileService;
import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.LongFunction;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                                                             @RequestParam("type") String type){
        return ResponseEntity.ok(ApiResponse.success(fileService.uploadFiles(files, type)));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId){
        //파일 서비스 호출
        FileDownloadDto fileDownloadDto = fileService.downloadFile(fileId);

        //파일명 인코딩 및 헤더 설정
        String encodedFileName = UriUtils.encode(fileDownloadDto.getOriginalName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        //Resource 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(fileDownloadDto.getFileType()))
                .contentLength(fileDownloadDto.getFileSize())
                .body(fileDownloadDto.getResource());
    }

    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> deleteFile(@PathVariable Long fileId){
        fileService.deleteFile(fileId);
        return ApiResponse.success("파일이 삭제되었습니다.");
    }
}
