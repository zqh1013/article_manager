package com.example.registration.dto;

import java.util.List;

public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer articleCount;
    private List<CategoryDTO> children;
    // Getters and Setters

    // 获取分类唯一标识
    public Long getId() {
        return id;
    }

    // 设置分类唯一标识
    public void setId(Long id) {
        this.id = id;
    }

    // 获取分类名称
    public String getName() {
        return name;
    }

    // 设置分类名称（支持中文及特殊字符）
    public void setName(String name) {
        this.name = name;
    }

    // 获取父分类ID（用于构建树形结构）
    public Long getParentId() {
        return parentId;
    }

    // 设置父分类ID
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    // 获取分类下文章数量
    public Integer getArticleCount() {
        return articleCount;
    }

    // 设置文章数量（包含校验逻辑占位）
    public void setArticleCount(Integer articleCount) {
        if (articleCount != null && articleCount < 0) {
            throw new IllegalArgumentException("文章数量不能为负数");
        }
        this.articleCount = articleCount;
    }

    // 获取子分类列表（支持树形结构展开）
    public List<CategoryDTO> getChildren() {
        return children;
    }

    // 设置子分类列表
    public void setChildren(List<CategoryDTO> children) {
        this.children = children;
    }
}
