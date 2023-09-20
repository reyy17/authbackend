package com.authLogin.AuthLogin.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SignUpDto {
    private String name;
    private String email;
    private String password;
    private Set<String> roles;
}
