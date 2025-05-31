package com.example.registration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateNicknameRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String newNickname;

    // Getters and setters
}