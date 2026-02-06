package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDate createdAt;
    private List<AttachmentDto> attachments;

    public static ColumnDetailResponse of(ColumnEntity column, List<AttachmentDto> attachments) {
        return ColumnDetailResponse.builder()
                .id(column.getId())
                .title(column.getTitle())
                .content(column.getContent())
                .author(column.getAuthor())
                .createdAt(column.getCreatedAt().toLocalDate())
                .attachments(attachments)
                .build();
    }
}