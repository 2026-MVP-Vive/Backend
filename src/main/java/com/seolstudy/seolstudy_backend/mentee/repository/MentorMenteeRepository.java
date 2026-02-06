package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.MentorMentee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorMenteeRepository extends JpaRepository<MentorMentee, Long> {

    Optional<MentorMentee> findByMenteeId(Long menteeId);

    List<MentorMentee> findAllByMentorId(Long mentorId);

    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);
}