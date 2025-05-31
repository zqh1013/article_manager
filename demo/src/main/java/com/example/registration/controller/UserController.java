package com.example.registration.controller;


import com.example.registration.dto.*;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestParam String email) {
        UserInfoRequest userInfo = userService.getUserInfo(email);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
                "nickname", userInfo.nickname(),
                "email", userInfo.email()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/question")
    public ResponseEntity<Map<String, Object>> getSecurityQuestion(@RequestParam String email) {
        SecurityQuestionRequest securityQuestion = userService.getQuestion(email);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
                "question", securityQuestion.question(),
                "answer", securityQuestion.answer()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String email,
                                            @Valid @RequestBody ChangePasswordRequest Request) {
        userService.changePassword(email, Request);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "密码修改成功"
        ));
    }

    @PostMapping("/update-nickname")
    public ResponseEntity<?> updateNickname(@RequestParam String email,
                                            @Valid @RequestBody UpdateNicknameRequest Request) {
        userService.updateNickname(email, Request);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "用户名修改成功"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                            @Valid @RequestBody ResetPasswordRequest Request) {
        userService.resetPassword(email, Request);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "密码重置成功"
        ));
    }

}
