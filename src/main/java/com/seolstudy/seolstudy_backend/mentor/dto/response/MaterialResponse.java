package com.seolstudy.seolstudy_backend.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class MaterialResponse {
    private Long id;
    private String fileName;
    private String downloadUrl;
}