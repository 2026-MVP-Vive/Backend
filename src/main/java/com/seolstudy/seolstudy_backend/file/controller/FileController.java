package com.seolstudy.seolstudy_backend.file.controller;

import com.seolstudy.seolstudy_backend.file.domain.File;
import com.seolstudy.seolstudy_backend.file.dto.FileDownloadDto;
import com.seolstudy.seolstudy_backend.file.dto.FilePreviewDto;
import com.seolstudy.seolstudy_backend.file.dto.FileUploadResponse;
import com.seolstudy.seolstudy_backend.file.service.FileService;
import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.core.io.ByteArrayResource;
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

    /** 파일 업로드 API 호출 */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                                                             @RequestParam("type") String type){
        return ResponseEntity.ok(ApiResponse.success(fileService.uploadFiles(files, type)));
    }

    /** 파일 다운로드 API 호출 */
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

    /** 파일 삭제 API 호출 */
    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> deleteFile(@PathVariable Long fileId){
        fileService.deleteFile(fileId);
        return ApiResponse.success("파일이 삭제되었습니다.");
    }

    /** 파일 미리보기 API 호출 */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> preview(@PathVariable Long fileId){
        FilePreviewDto filePreviewDto = fileService.getFileInfo(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(filePreviewDto.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(filePreviewDto.getResource());
    }

    /** 썸네일 API 호출 */
    @GetMapping("/{fileId}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(
            @PathVariable Long fileId,
            @RequestParam(defaultValue = "200") Integer width,
            @RequestParam(defaultValue = "200") Integer height) {

        byte[] imageBytes = fileService.generateThumbnail(fileId, width, height);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 썸네일은 항상 JPEG로 응답
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(new ByteArrayResource(imageBytes));
    }
}
