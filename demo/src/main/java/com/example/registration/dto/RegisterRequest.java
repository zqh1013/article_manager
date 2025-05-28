package com.example.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    private String nickname;
    private String email;
    private String password;
    private String confirmPassword;

    @NotBlank(message = "请输入您的称呼")
    @Size(min = 2, max = 20, message = "称呼长度需2-20个字符")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname.trim();
    }

    @NotBlank(message = "请输入邮箱地址")
    @Email(message = "邮箱格式不正确")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    @NotBlank(message = "请输入密码")
    @Size(min = 8, message = "密码至少需要8位字符")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    @NotBlank(message = "请再次输入密码")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword.trim();
    }
}