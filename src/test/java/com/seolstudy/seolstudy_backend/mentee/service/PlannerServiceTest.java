package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.MentorMentee;
import com.seolstudy.seolstudy_backend.mentee.domain.PlannerCompletion;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.dto.PlannerCompleteRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.PlannerCompletionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.PlannerCompletionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.SubmissionRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlannerServiceTest {

    @InjectMocks
    private PlannerService plannerService;

    @Mock
    private PlannerCompletionRepository plannerCompletionRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MentorMenteeRepository mentorMenteeRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("플래너 마감 성공: 멘티 요청 시 즉시 완료 처리")
    void completeDailyPlanner_Success_RegardlessOfTaskConfirmation() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        // 아직 완료 안됨
        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(false);

        // 멘토 및 유저 정보 모킹 (알림 발송용)
        MentorMentee mentorMentee = new MentorMentee(100L, menteeId); // mentorId=100
        given(mentorMenteeRepository.findByMenteeId(menteeId)).willReturn(Optional.of(mentorMentee));

        User menteeUser = new User(); // User 생성이 필요함 (기본 생성자나 빌더 확인 필요, 간단히 가정)
        // User 객체의 구체적인 생성 방식에 따라 다를 수 있으나, 여기서는 Mock으로 처리하거나 기본 객체 사용
        // given(userRepository.findById(menteeId)).willReturn(Optional.of(menteeUser));
        // 위 코드는 User 객체 내부 구현에 의존하므로, Mock을 사용하는 것이 안전할 수 있음.
        User mockUser = new User();
        // User 엔티티에 setName 등이 있다고 가정하거나 리플렉션 사용.
        // 테스트 코드 단순화를 위해 Mock 객체가 나을 수도 있으나, User가 Final 클래스가 아니면 가능.
        // 여기서는 간단히 User를 리턴하도록.
        given(userRepository.findById(menteeId)).willReturn(Optional.of(mockUser));

        // 저장될 객체 모킹
        PlannerCompletion savedCompletion = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.now())
                .id(1L)
                .build();
        given(plannerCompletionRepository.save(any(PlannerCompletion.class))).willReturn(savedCompletion);

        // when
        PlannerCompleteRequest request = PlannerCompleteRequest.builder().tasks(List.of(10L)).build();
        PlannerCompletionResponse response = plannerService.completeDailyPlanner(menteeId, date, request);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        verify(plannerCompletionRepository).save(any(PlannerCompletion.class));
        verify(notificationService).createNotification(
                any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("플래너 마감 실패: 할 일이 하나도 없음 (선택적)")
    void completeDailyPlanner_NoTasksInRequest() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(false);

        // 멘토 및 유저 정보 모킹 (알림 발송용)
        MentorMentee mentorMentee = new MentorMentee(100L, menteeId); // mentorId=100
        given(mentorMenteeRepository.findByMenteeId(menteeId)).willReturn(Optional.of(mentorMentee));
        User mockUser = new User();
        given(userRepository.findById(menteeId)).willReturn(Optional.of(mockUser));

        // 저장 모킹
        PlannerCompletion savedCompletion = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.now())
                .build();
        given(plannerCompletionRepository.save(any(PlannerCompletion.class))).willReturn(savedCompletion);

        // when
        PlannerCompleteRequest request = PlannerCompleteRequest.builder().tasks(List.of()).build();
        PlannerCompletionResponse response = plannerService.completeDailyPlanner(menteeId, date, request);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        verify(taskRepository, org.mockito.Mockito.never()).findAllById(any());
        verify(plannerCompletionRepository).save(any(PlannerCompletion.class));
        verify(notificationService).createNotification(
                any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("플래너 마감: 이미 완료된 경우 (멱등성)")
    void completeDailyPlanner_AlreadyCompleted() {
        // given
        Long menteeId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 27);

        given(plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)).willReturn(true);

        PlannerCompletion existing = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.of(2025, 1, 27, 22, 0))
                .build();
        given(plannerCompletionRepository.findByMenteeIdAndPlanDate(menteeId, date)).willReturn(Optional.of(existing));

        // when
        PlannerCompleteRequest request = PlannerCompleteRequest.builder().tasks(List.of(10L)).build();
        PlannerCompletionResponse response = plannerService.completeDailyPlanner(menteeId, date, request);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getCompletedAt()).isEqualTo(existing.getCompletedAt());
        verify(plannerCompletionRepository, org.mockito.Mockito.never()).save(any(PlannerCompletion.class));
        verify(notificationService, org.mockito.Mockito.never()).createNotification(
                any(), any(), any(), any(), any());
    }
}
