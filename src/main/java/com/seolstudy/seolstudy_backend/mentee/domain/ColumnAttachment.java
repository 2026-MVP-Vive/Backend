package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "column_attachments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_column_attachments", columnNames = { "column_id", "file_id" })
})
@Getter
@NoArgsConstructor
@ToString
public class ColumnAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "column_id", nullable = false)
    private Long columnId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;
}
