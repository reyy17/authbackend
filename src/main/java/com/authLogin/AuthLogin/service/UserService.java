package com.authLogin.AuthLogin.service;

import com.authLogin.AuthLogin.entity.User;

import java.util.Optional;

public interface UserService {
     Optional<User> findUserByEmail(String email);
     Optional<User> findUserByResetToken(String resetToken);
     void save(User user);
}
