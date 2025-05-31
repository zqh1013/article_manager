package com.example.registration.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false, length = 50)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "article_count", columnDefinition = "INT DEFAULT 0")
    private Integer articleCount = 0;

    @Column(name = "user_id", nullable = false)  // 新增用户关联字段[1,4](@ref)
    private Long userId;

    @Transient  // 非数据库字段
    private List<Category> children = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Category> getChildren() {
        return new ArrayList<>(children); // 返回不可变副本避免直接修改[3](@ref)
    }

    public void addChild(Category child) {
        children.add(child);
        child.setParentId(this.parentId);
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        if (articleCount < 0) {
            throw new IllegalArgumentException("文章数量不能为负数");
        }
        this.articleCount = articleCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }
        this.userId = userId;
    }
}