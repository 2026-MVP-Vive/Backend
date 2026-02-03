package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenteeService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse addTask(Long menteeId, TaskRequest request) {
        // Mentee ID is used as createdBy for self-assigned tasks
        Task task = new Task(
                menteeId,
                request.getTitle(),
                request.getDate(),
                request.getSubject(),
                menteeId
        );
        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }
}
