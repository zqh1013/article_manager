package com.example.registration.repository;

import com.example.registration.dto.ArticleWithCategoryDTO;
import com.example.registration.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitleIgnoreCase(String title);

    // 通过 userId 查询文章
    @Query("SELECT NEW com.example.registration.dto.ArticleWithCategoryDTO(" +
            "a.id, a.title, a.categoryId, a.tags, a.visibility, a.createTime, c.name) " +
            "FROM Article a " +
            "LEFT JOIN Category c ON a.categoryId = c.id AND a.userId = c.userId " +
            "WHERE a.userId = :userId")
    Page<ArticleWithCategoryDTO> findArticlesWithCategoryByUserId(
            @Param("userId") Long userId,
            Pageable pageable);
}
