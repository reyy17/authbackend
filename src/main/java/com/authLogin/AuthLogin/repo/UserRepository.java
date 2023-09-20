package com.authLogin.AuthLogin.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import com.authLogin.AuthLogin.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String username);
    Boolean existsByName(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
}