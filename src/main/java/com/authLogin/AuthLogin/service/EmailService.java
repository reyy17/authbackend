package com.authLogin.AuthLogin.service;

import org.springframework.mail.SimpleMailMessage;


public interface EmailService {
     void sendEmail(SimpleMailMessage email);
}
