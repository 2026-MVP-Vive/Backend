package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.MentorMentee;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.dto.MenteeProfileResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeProfileService {

        private final UserRepository userRepository;
        private final MentorMenteeRepository mentorMenteeRepository;
        private final TaskRepository taskRepository;

        @Transactional
        public MenteeProfileResponse getMenteeProfile(Long menteeId) {
                User mentee = userRepository.findById(menteeId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Mentee not found with id: " + menteeId));

                MentorMentee mentorMentee = mentorMenteeRepository.findByMenteeId(menteeId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Mentor not assigned for mentee: " + menteeId));

                User mentor = userRepository.findById(mentorMentee.getMentorId())
                                .orElseThrow(
                                                () -> new IllegalArgumentException("Mentor not found with id: "
                                                                + mentorMentee.getMentorId()));

                long totalStudyDays = taskRepository.countDistinctTaskDateByMenteeId(menteeId);

                String profileImageUrl = mentee.getProfileImageId() != null
                                ? "/api/v1/files/profile/" + mentee.getProfileImageId()
                                : null;

                return MenteeProfileResponse.builder()
                                .id(mentee.getId())
                                .name(mentee.getName())
                                .profileImageUrl(profileImageUrl)
                                .mentorName(mentor.getName())
                                .startDate(LocalDate.from(mentorMentee.getCreatedAt()))
                                .totalStudyDays(totalStudyDays)
                                .build();
        }
}
