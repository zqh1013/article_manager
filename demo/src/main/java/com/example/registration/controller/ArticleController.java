package com.example.registration.controller;

import com.example.registration.dto.ArticleCreateRequest;
import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.model.Article;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final UserRepository userRepository;

    private final ArticleService articleService;

    public ArticleController(UserRepository userRepository, ArticleService articleService) {
        this.userRepository = userRepository;
        this.articleService = articleService;
    }

    @PostMapping("/article_editor")
    public ResponseEntity<?> createArticle(@RequestParam String email,
                                           @RequestBody ArticleCreateRequest request) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Article article = articleService.createArticle(userId, request);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "文章创建成功",
                "data", article
        ));
    }
}
