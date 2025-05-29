package com.example.registration.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 100)
    private String title;

    @Setter
    @Column(name = "category_id")
    private Long categoryId;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "JSON")
    @Size(max = 5, message = "最多添加5个标签")
    private List<@Size(max=15, message = "标签最长15字符") String> tags = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "user_id", nullable = false)
    private String userId;  // 关联用户标识

    @Setter
    @Column(name = "visibility", nullable = false)
    private String visibility;


    public void setTitle(String title) {
        this.title = title.trim();
    }


    public void setTags(List<String> tags) {
        this.tags = (List<String>) tags;
    }

    public void setContent(Object content) {
        this.content = content == null ? "" : content.toString();
    }

    public void setUserId(Long userId) {
        this.userId = userId.toString();
    }
}

// 列表转换器
@Converter
class StringListConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转换失败");
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败");
        }
    }
}