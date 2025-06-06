package com.example.registration.model;

import jakarta.persistence.*;

//@Entity
//public class User {
//    private Long id;
//    private String nickname;
//    private String email;
//    private String password;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    @Column(unique = true, nullable = false)
//    public String getNickname() {
//        return nickname;
//    }
//
//    public void setNickname(String nickname) {
//        this.nickname = nickname.trim();
//    }
//
//    @Column(unique = true, nullable = false)
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email.trim().toLowerCase();
//    }
//
//    @Column(nullable = false)
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}
@Entity
@Table(name = "users") // 明确指定表名（避免使用 MySQL 保留字 user）
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", unique = true, nullable = false, length = 50)
    private String nickname;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    // Getters 和 Setters（保持 trim 逻辑）
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) {
        this.nickname = nickname.trim(); // 自动去除首尾空格
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email.trim().toLowerCase(); // 统一小写格式
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }
}

