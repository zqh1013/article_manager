package com.example.registration.dto;

import com.example.registration.model.Article;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ArticleWithSearchDTO {
    private Long id;
    private String title;
    private Long categoryId;
    private String visibility;
    private LocalDateTime createTime;
    private String categoryName;
    private String reviewStatus;

    // 全参构造器（JPA 映射必需）
    public ArticleWithSearchDTO(
            Long id,
            String title,
            Long categoryId,
            String visibility,
            Timestamp createTime,
            String categoryName,
            Article.ReviewStatus reviewStatus
    ) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
        this.visibility = visibility;
        this.createTime = createTime.toLocalDateTime();
        this.categoryName = categoryName;
        this.reviewStatus = reviewStatus.name();
    }

    // Getter 方法
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Long getCategoryId() { return categoryId; }
    public String getVisibility() { return visibility; }
    public LocalDateTime getCreateTime() { return createTime; }
    public String getCategoryName() { return categoryName; }
    public String getReviewStatus() { return reviewStatus; }
}