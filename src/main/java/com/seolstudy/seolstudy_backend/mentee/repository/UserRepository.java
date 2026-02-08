package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long aLong);
    Optional<User> findByLoginId(String loginId);

    @Query("SELECT u.fcm_Token FROM User u " +
            "JOIN MentorMentee mm ON mm.mentorId = u.id " +
            "WHERE mm.menteeId = :menteeId")
    Optional<String> findMentorTokenByMenteeId(@Param("menteeId") Long menteeId);
}