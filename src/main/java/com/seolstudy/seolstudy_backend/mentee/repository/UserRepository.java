package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
