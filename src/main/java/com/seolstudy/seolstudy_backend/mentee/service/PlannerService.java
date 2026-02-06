package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.PlannerCompletion;
import com.seolstudy.seolstudy_backend.mentee.dto.PlannerCompletionResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.PlannerCompletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlannerService {

    private final PlannerCompletionRepository plannerCompletionRepository;

    @Transactional
    public PlannerCompletionResponse completeDailyPlanner(Long menteeId, LocalDate date) {
        // 이미 완료된 상태인지 확인 (Optional, 멱등성 보장을 위해)
        if (plannerCompletionRepository.existsByMenteeIdAndPlanDate(menteeId, date)) {
            PlannerCompletion existing = plannerCompletionRepository.findByMenteeIdAndPlanDate(menteeId, date).get();
            return PlannerCompletionResponse.builder()
                    .date(existing.getPlanDate())
                    .completedAt(existing.getCompletedAt())
                    .status("COMPLETED")
                    .build();
        }

        PlannerCompletion completion = PlannerCompletion.builder()
                .menteeId(menteeId)
                .planDate(date)
                .completedAt(LocalDateTime.now()) // Auditing may handle this, but setting for response consistency
                .build();

        // Note: createdAt is handled by @CreatedDate if Auditing is enabled, but we
        // manually set completedAt above for DTO.
        // If entity uses @CreatedDate for completedAt, we should let JPA handle it and
        // flush/refetch or use return value.
        // Assuming manual setting for now based on Entity definition without
        // @CreatedDate on completedAt (Wait, I added @CreatedDate)
        // If @CreatedDate is present, it will be overwritten by JPA on save if null?
        // No, it populates only if null or always?
        // Let's rely on the saved entity.

        PlannerCompletion saved = plannerCompletionRepository.save(completion);

        return PlannerCompletionResponse.builder()
                .date(saved.getPlanDate())
                .completedAt(saved.getCompletedAt() != null ? saved.getCompletedAt() : LocalDateTime.now())
                .status("COMPLETED")
                .build();
    }
}
