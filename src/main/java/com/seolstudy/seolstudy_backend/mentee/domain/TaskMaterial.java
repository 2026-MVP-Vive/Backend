package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "task_materials")
@Getter
@NoArgsConstructor
@ToString
public class TaskMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    public TaskMaterial(Long taskId, Long fileId) {
        this.taskId = taskId;
        this.fileId = fileId;
    }
}