package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnListResponse {
    private List<ColumnDto> columns;
    private PaginationDto pagination;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationDto {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
