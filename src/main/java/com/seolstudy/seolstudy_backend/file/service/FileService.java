package com.seolstudy.seolstudy_backend.file.service;

import com.seolstudy.seolstudy_backend.file.domain.File;
import com.seolstudy.seolstudy_backend.file.dto.FileDownloadDto;
import com.seolstudy.seolstudy_backend.file.dto.FileUploadResponse;
import com.seolstudy.seolstudy_backend.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ResourceClosedException;
import org.hibernate.usertype.BaseUserTypeSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 파일 다운로드, 업로드, 업데이트, 삭제, 프로필 파일 경로 반환 로직을 구현한 서비스 클래스입니다.
 * */

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileRepository fileRepository;
    private final S3Client s3Client;
    private final SecurityUtil securityUtil;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public List<FileUploadResponse> uploadFiles(List<MultipartFile> multipartFiles, String type){
        if(multipartFiles == null || multipartFiles.isEmpty()){
            throw new BusinessException("업로드할 파일이 존재하지 않습니다.", ErrorCode.NOT_FOUND);
        }
        return multipartFiles.stream()
                .map(file -> uploadFile(file, type))
                .collect(Collectors.toList());
    }

    /** 파일 업로드 */
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile multipartFile, String type){
        String originalName = multipartFile.getOriginalFilename();
        /** 파일명 중복 방지를 위해 S3 버킷에 저장할 파일 이름 앞에 랜덤 ID를 부여 */
        String path = type.toLowerCase() + "/" + LocalDate.now();
        String storedName = path + "/" + UUID.randomUUID().toString() + "_" + originalName;
        String contentType = multipartFile.getContentType();

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(storedName)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
        } catch(IOException e){
            log.error("파일 업로드 실패: {}", originalName, e);
            throw new BusinessException("파일 업로드 도중 에러가 발생했습니다.", ErrorCode.INTERNAL_ERROR);
        }

        String s3Url = s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucket).key(storedName).build()).toString();

        File fileEntity = fileRepository.save(File
                .builder()
                .originalName(originalName)
                .storedName(storedName)
                .filePath(s3Url)
                .fileType(contentType)
                .fileSize(multipartFile.getSize())
                .category(File.FileCategory.valueOf(type.toUpperCase()))
                .uploaderId(securityUtil.getCurrentUserId())
                .build());
        return FileUploadResponse.builder()
                .id(fileEntity.getId())
                .fileName(originalName)
                .fileType(extractExtension(originalName))
                .fileSize(fileEntity.getFileSize())
                .url("/api/v1/files/" + fileEntity.getId())
                .build();
    }

    @Transactional
    public Resource loadFileAsResource(String storedName){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(storedName)
                    .build();
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            log.info("S3 다운로드 요청 - 버킷: {}, 키: {}", bucket, storedName);
            return new InputStreamResource(s3Object);
        } catch(S3Exception e){
            throw new BusinessException("파일을 조회하거나 찾을 수 없습니다.", ErrorCode.NOT_FOUND);
        }
    }

    @Transactional
    public FileDownloadDto downloadFile(Long fileId){
        //파일이 존재하는지 확인
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("파일을 조회하거나 찾을 수 없습니다.", ErrorCode.NOT_FOUND));

        //s3 오브젝트를 가져옴
        Resource resource = loadFileAsResource(fileEntity.getStoredName());

        //FileDownloadDto 형식으로 컨트롤러에 반환
        return FileDownloadDto.builder()
                .originalName(fileEntity.getOriginalName())
                .fileType(fileEntity.getFileType())
                .fileSize(fileEntity.getFileSize())
                .resource(resource)
                .build();
    }

    @Transactional
    public ApiResponse<Void> deleteFile(@PathVariable Long fileId) {
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("해당 파일을 찾을 수 없습니다.", ErrorCode.NOT_FOUND));

        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileEntity.getStoredName())
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch(S3Exception e){
            throw new BusinessException("파일 삭제 중 에러가 발생했습니다.", ErrorCode.INTERNAL_ERROR);
        }

        fileRepository.deleteById(fileId);
        return ApiResponse.success("파일이 삭제되었습니다.");
    }


    /** 파일명 맨뒤 확장자만 반환하는 메서드 ex) ex.pdf에서 pdf를 반환 */
    private String extractExtension(String fileName){
        if(fileName == null || !fileName.contains(".")){
            return "UNKNOWN";
        }
        return fileName.substring(fileName.indexOf(".") + 1).toUpperCase();
    }

    /** 회원 프로필 이미지가 저장된 경로를 반환하는 메서드 */
    @Transactional
    public String getProfileImageUrl(long id){
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new BusinessException("프로필 이미지를 찾을 수 없습니다.", ErrorCode.NOT_FOUND));
        return file.getFilePath();
    }
}
