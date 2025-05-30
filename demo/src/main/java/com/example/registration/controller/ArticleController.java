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


// 新增导入
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    // 新增分页查询API
    @GetMapping
    public ResponseEntity<?> getArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String email) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        // 创建分页请求 (page从1开始计数)
        Pageable pageable = PageRequest.of(page - 1, limit);

        // 调用Service获取分页数据
        Page<Article> articlePage = articleService.getArticles(pageable, userId);

        return ResponseEntity.ok().body(Map.of(
                "total", articlePage.getTotalElements(),
                "data", articlePage.getContent()
        ));
    }
}
