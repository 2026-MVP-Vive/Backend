package com.seolstudy.seolstudy_backend.mentee.dto;

import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@ToString
public class TaskResponse {
    private Long id;
    private String title;
    private LocalDate date;
    private Subject subject;
    private boolean isMentorAssigned;

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getTaskDate(),
                task.getSubject(),
                task.isMentorAssigned()
        );
    }
}
