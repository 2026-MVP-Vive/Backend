package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.ColumnEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnDto {
    private Long id;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private LocalDate createdAt;

    public static ColumnDto from(ColumnEntity column) {
        String thumbnailUrl = null;
        if (column.getThumbnailId() != null) {
            thumbnailUrl = "/api/v1/files/column/" + column.getThumbnailId() + "/thumbnail";
            // Note: This URL pattern is assumed based on requirement examples.
            // The requirement example URL is "/api/v1/files/column/1/thumbnail".
            // Ideally, FileService or a helper should generate this URL.
            // For now, hardcoding the pattern as requested in the example.
        }

        return ColumnDto.builder()
                .id(column.getId())
                .title(column.getTitle())
                .summary(column.getSummary())
                .thumbnailUrl(thumbnailUrl)
                .createdAt(column.getCreatedAt().toLocalDate())
                .build();
    }
}
