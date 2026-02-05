package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.repository.FileRepository;
import com.seolstudy.seolstudy_backend.mentee.domain.ColumnAttachment;
import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import com.seolstudy.seolstudy_backend.mentee.dto.AttachmentDto;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDto;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnListResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.ColumnAttachmentRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColumnService {

        private final ColumnRepository columnRepository;

        private final ColumnAttachmentRepository columnAttachmentRepository;
        private final FileRepository fileRepository;

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

        public ColumnDetailResponse getColumnDetail(Long columnId) {
                ColumnEntity column = columnRepository.findById(columnId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Column not found with id: " + columnId));

                List<ColumnAttachment> columnAttachments = columnAttachmentRepository.findAllByColumnId(columnId);
                List<Long> fileIds = columnAttachments.stream()
                                .map(ColumnAttachment::getFileId)
                                .collect(Collectors.toList());

                List<File> files = fileRepository.findAllById(fileIds);

                List<AttachmentDto> attachments = files.stream()
                                .map(file -> AttachmentDto.builder()
                                                .id(file.getId())
                                                .fileName(file.getOriginalName())
                                                .downloadUrl("/api/v1/files/" + file.getId() + "/download")
                                                .build())
                                .collect(Collectors.toList());

                return ColumnDetailResponse.of(column, attachments);
        }
}
