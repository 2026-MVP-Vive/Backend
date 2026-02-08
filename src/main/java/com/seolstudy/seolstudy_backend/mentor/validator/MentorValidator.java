package com.seolstudy.seolstudy_backend.mentor.validator;

import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.global.error.GlobalExceptionHandler;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.domain.UserRole;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 멘토 API의 모든 로직에 반복되는 “권한·소유권 검증”을 한 곳에 모으기 위해
@Component
@RequiredArgsConstructor
public class MentorValidator {

    private final MentorMenteeRepository mentorMenteeRepository;

    // 멘토 권한 체크
    public void validateMentor(User mentor) {
        if (mentor.getRole() != UserRole.MENTOR) {
            throw new IllegalArgumentException("멘토 권한이 없습니다.");
        }
    }

    // 담당 멘티 여부 체크
    public void validateMyStudent(Long mentorId, Long studentId) {
        boolean exists = mentorMenteeRepository.existsByMentorIdAndMenteeId(
                mentorId, studentId);

        if (!exists) {
            throw new IllegalArgumentException("해당 멘티에 대한 접근 권한이 없습니다.");
        }
    }
}
