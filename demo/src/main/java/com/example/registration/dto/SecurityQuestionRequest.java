package com.example.registration.dto;

public class SecurityQuestionRequest {
    public String question;
    public String answer;
    public SecurityQuestionRequest(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String question() {
        return question;
    }
    public String answer() {
        return answer;
    }
}
