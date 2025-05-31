package com.example.registration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter // 自动生成所有getter
@Setter // 自动生成所有setter
public class ArticleWithShareDTO {
    private Long id;
    private String title;
    private List<String> tags;
    private String visibility;
    private String create_time;
    private String author;

    // 构造函数必须与JPQL中的字段顺序匹配
    public ArticleWithShareDTO(
            Long id,
            String title,
            List<String> tags,
            String visibility,
            LocalDateTime createTime,
            String author) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.visibility = visibility;
        this.create_time = createTime != null ?
                createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.author= author;
    }
}