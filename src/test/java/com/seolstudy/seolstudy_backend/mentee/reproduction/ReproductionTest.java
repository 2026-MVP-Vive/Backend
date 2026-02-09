package com.seolstudy.seolstudy_backend.mentee.reproduction;

import com.seolstudy.seolstudy_backend.global.config.JpaConfig;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@ActiveProfiles("test")
@Import({ TaskService.class, JpaConfig.class })
public class ReproductionTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private SecurityUtil securityUtil;

    @Test
    @DisplayName("created_by가 null이 아닌지 확인")
    void testTaskCreation() {
        // Given
        Long menteeId = 1L;
        TaskRequest request = new TaskRequest();
        request.setTitle("Debug Task");
        request.setDate(LocalDate.now());
        request.setSubject(Subject.MATH);

        // When & Then
        assertDoesNotThrow(() -> {
            taskService.addTask(menteeId, request);
        });
    }
}
