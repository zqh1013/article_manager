package com.example.registration.service;

import com.example.registration.dto.RegisterRequest;
import com.example.registration.exception.exception.EmailNotRegisteredException;
import com.example.registration.exception.exception.IncorrectPasswordException;
import com.example.registration.exception.exception.PasswordNotConfirmException;
import com.example.registration.exception.exception.RepeatedEmailException;
import com.example.registration.model.User;
import com.example.registration.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(@Valid RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordNotConfirmException("两次输入密码不一致");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RepeatedEmailException("该邮箱已被注册");
        }

        User user = new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword((request.getPassword()));

        userRepository.save(user);
    }

    public String authenticate(String email, String rawPassword){
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new EmailNotRegisteredException("该邮箱未注册"));

        if (!rawPassword.equals(user.getPassword())) {
            throw new IncorrectPasswordException("密码不正确");
        }

        return generateBasicAuthToken(user.getEmail(), user.getPassword()); // 生成JWT令牌
    }

    private String generateBasicAuthToken(String email, String password) {
        String authString = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
    }
}