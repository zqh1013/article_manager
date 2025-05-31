package com.example.registration.repository;

import com.example.registration.dto.ArticleWithCategoryDTO;
import com.example.registration.model.Article;
import com.example.registration.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitleIgnoreCase(String title);


    // 暂未实现tags的筛选(数据库中tags以json格式存储)
    @Query("""
        SELECT NEW com.example.registration.dto.ArticleWithCategoryDTO(
            a.id,
            a.title,
            a.categoryId,
            a.tags,
            a.visibility,
            a.createTime,
            c.name
        )
        FROM Article a
        LEFT JOIN Category c ON a.categoryId = c.id AND a.userId = c.userId
        WHERE a.userId = :userId

        AND (:category IS NULL OR c.id = :category)
        AND (:startDate IS NULL OR a.createTime >= :startDate)
        AND (:endDate IS NULL OR a.createTime <= :endDate)
        """)
    Page<ArticleWithCategoryDTO> findArticlesWithConditions(
            @Param("userId") Long userId,
            @Param("tags") String tagsJson,
            @Param("category") Long category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 通过 userId 查询文章
//    @Query("SELECT NEW com.example.registration.dto.ArticleWithCategoryDTO(" +
//            "a.id, a.title, a.categoryId, a.tags, a.visibility, a.createTime, c.name) " +
//            "FROM Article a " +
//            "LEFT JOIN Category c ON a.categoryId = c.id AND a.userId = c.userId " +
//            "WHERE a.userId = :userId")
//    Page<ArticleWithCategoryDTO> findArticlesWithCategoryByUserId(
//            @Param("userId") Long userId,
//            Pageable pageable);


    Optional<Article> findById(@Param("id") Long id);
}
