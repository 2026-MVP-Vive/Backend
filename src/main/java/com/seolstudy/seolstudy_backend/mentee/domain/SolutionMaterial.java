package com.seolstudy.seolstudy_backend.mentee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "solution_materials", uniqueConstraints = {
        @UniqueConstraint(name = "uk_solution_materials", columnNames = { "solution_id", "file_id" })
})
@Getter
@NoArgsConstructor
@ToString
public class SolutionMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solution_id", nullable = false)
    private Long solutionId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    public SolutionMaterial(Long solutionId, Long fileId) {
        this.solutionId = solutionId;
        this.fileId = fileId;
    }
}