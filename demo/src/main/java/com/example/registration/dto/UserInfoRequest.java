package com.example.registration.dto;


public class UserInfoRequest {
    public String nickname;
    public String email;

    public UserInfoRequest(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public Object nickname() {
        return nickname;
    }

    public Object email() {
        return email;
    }


    // Getters and setters
}