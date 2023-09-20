package com.authLogin.AuthLogin.service;

import com.authLogin.AuthLogin.repo.UserRepository;
import com.authLogin.AuthLogin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public Optional findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional findUserByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }


    @Override
    public void save(User user) {
        userRepository.save(user);
    }


}
