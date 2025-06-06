package com.example.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "请输入邮箱地址")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "请输入密码")
    private String password;

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}