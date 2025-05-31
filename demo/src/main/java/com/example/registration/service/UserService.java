package com.example.registration.service;

import com.example.registration.dto.*;
import com.example.registration.model.User;
import com.example.registration.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfoRequest getUserInfo(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserInfoRequest(user.getNickname(), user.getEmail());
    }

    public void changePassword(String email, ChangePasswordRequest Request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //验证旧密码
        if (!Request.getOldPassword().equals(user.getPassword())){
            throw new SecurityException("Current password is incorrect");
        }
        //验证两次密码输入是否相等
        if (!Request.getNewPassword().equals(Request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }
        user.setPassword(Request.getNewPassword());
        userRepository.save(user);
    }

    public void updateNickname(String email, UpdateNicknameRequest Request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userRepository.existsByNickname(Request.getNewNickname())) {
            throw new IllegalArgumentException("nickname already taken");
        }
        user.setNickname(Request.getNewNickname());
        userRepository.save(user);
    }

    public SecurityQuestionRequest getQuestion(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new SecurityQuestionRequest(user.getQuestion(), user.getAnswer());
    }

    public void resetPassword(String email, ResetPasswordRequest Request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //验证两次密码输入是否相等
        if (!Request.getNewPassword().equals(Request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }
        user.setPassword(Request.getNewPassword());
        userRepository.save(user);
    }

}
