package com.example.registration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // 自动生成所有getter
@Setter // 自动生成所有setter
@NoArgsConstructor
public class ArticleWithCategoryDTO {
    private Long id;
    private String title;
    private Long category_id;
    private List<String> tags = new ArrayList<>();;
    private String visibility;

    private String create_time;

    private String category_name; // 新增的分类名称字段

    // 构造函数必须与JPQL中的字段顺序匹配
    public ArticleWithCategoryDTO(
            Long id,
            String title,
            Long categoryId,
            List<String> tags,
            String visibility,
            LocalDateTime createTime,
            String categoryName) {
        this.id = id;
        this.title = title;
        this.category_id = categoryId;
        this.tags = tags;
        this.visibility = visibility;
        this.create_time = createTime != null ?
                createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.category_name = categoryName;
    }
    public ArticleWithCategoryDTO(
            Long id,
            String title,
            Long categoryId,
            String visibility,
            String createTime,
            String categoryName) {
        this.id = id;
        this.title = title;
        this.category_id = categoryId;
        this.visibility = visibility;
        this.create_time = createTime;
        this.category_name = categoryName;
    }
}
