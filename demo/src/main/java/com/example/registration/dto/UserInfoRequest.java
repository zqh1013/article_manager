package com.example.registration.dto;


public class UserInfoRequest {
    public String nickname;
    public String email;
    public boolean isAdmin;

    public UserInfoRequest(String nickname, String email,boolean isAdmin) {
        this.nickname = nickname;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public Object nickname() {
        return nickname;
    }

    public Object email() {
        return email;
    }
    public Object isAdmin() {
        return isAdmin;
    }

    // Getters and setters
}