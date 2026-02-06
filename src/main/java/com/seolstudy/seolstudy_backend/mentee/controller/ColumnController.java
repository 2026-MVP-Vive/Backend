package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.mentee.dto.ApiResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnListResponse;
import com.seolstudy.seolstudy_backend.mentee.service.ColumnService;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mentee")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping("/columns")
    public ApiResponse<ColumnListResponse> getColumns(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        ColumnListResponse response = columnService.getColumns(pageable);
        return ApiResponse.success(response);
    }

    @GetMapping("/columns/{columnId}")
    public ApiResponse<ColumnDetailResponse> getColumnDetail(@PathVariable Long columnId) {
        ColumnDetailResponse response = columnService.getColumnDetail(columnId);
        return ApiResponse.success(response);
    }
}