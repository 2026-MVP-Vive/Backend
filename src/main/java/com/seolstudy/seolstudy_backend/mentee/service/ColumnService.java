package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDto;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnListResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColumnService {

    private final ColumnRepository columnRepository;

    public ColumnListResponse getColumns(Pageable pageable) {
        Page<ColumnEntity> columnPage = columnRepository.findAll(pageable);

        List<ColumnDto> columns = columnPage.getContent().stream()
                .map(ColumnDto::from)
                .collect(Collectors.toList());

        ColumnListResponse.PaginationDto pagination = ColumnListResponse.PaginationDto.builder()
                .page(columnPage.getNumber())
                .size(columnPage.getSize())
                .totalElements(columnPage.getTotalElements())
                .totalPages(columnPage.getTotalPages())
                .build();

        return ColumnListResponse.builder()
                .columns(columns)
                .pagination(pagination)
                .build();
    }
}
