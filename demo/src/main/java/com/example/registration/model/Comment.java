package com.example.registration.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String email;

    @CreatedDate
    @Column(
            name = "create_time",
            nullable = false,
            updatable = false  // 禁止更新[5,7](@ref)
    )
    private LocalDateTime createTime;

    @Column(name = "article_id", nullable = false)
    private Long articleId;
}
