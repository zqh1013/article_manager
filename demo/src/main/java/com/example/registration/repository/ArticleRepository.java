package com.example.registration.repository;

import com.example.registration.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitleIgnoreCase(String title);

    // 通过 userId 查询文章
    Page<Article> findByUserId(Long userId, Pageable pageable);
    // 通过标题和 userId 查询
    Page<Article> findByTitleContainingAndUserId(String title, Long userId, Pageable pageable);

    Page<Article> findByTitleContaining(String title, Pageable pageable);
//    Page<Article> findByAuthorContaining(String author, Pageable pageable);
//    Page<Article> findByTitleContainingAndAuthorContaining(
//            String title, String author, Pageable pageable);
}
