package com.authLogin.AuthLogin.dto;

import lombok.Data;


@Data
public class ResetPasswordDto {
    private String resetToken;
    private String newPassword;
}
