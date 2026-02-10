package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Subject is required")
    private Subject subject;
}