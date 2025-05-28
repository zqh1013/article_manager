package com.example.registration.repository;

import com.example.registration.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitleIgnoreCase(String title);
}
