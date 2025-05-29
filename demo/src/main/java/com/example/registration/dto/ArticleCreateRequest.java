package com.example.registration.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ArticleCreateRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 100, message = "标题最长100个字符")
    private String title;

    @NotBlank(message = "必须有分类标签")
    @Size(min = 1, max = 50, message = "分类名称最长50个字符")
    private Long categoryId;

    @Size(max = 5, message = "最多添加5个标签")
    private List<@Size(max = 15, message = "单个标签最长15字符") String> tags;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @NotBlank(message = "必须选择可见性")
    private String visibility;

    // 手动编写getter/setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

