package com.seolstudy.seolstudy_backend.mentor.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MentorTaskCreateMultipartRequest {

    private String title;
    private LocalDate date;
    private Long goalId;                 // nullable
    private List<MultipartFile> materials; // 🔥 파일 직접 업로드
}