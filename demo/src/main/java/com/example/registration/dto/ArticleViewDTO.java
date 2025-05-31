package com.example.registration.dto;


import lombok.Getter;
import lombok.Setter;

@Getter // 自动生成所有getter
@Setter // 自动生成所有setter
public class ArticleViewDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String createTime;
    private String categoryName;
    private String aiSummary;
    // getters/setters
}