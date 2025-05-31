package com.example.registration.controller;

import com.example.registration.dto.ArticleCreateRequest;
import com.example.registration.dto.ArticleViewDTO;
import com.example.registration.dto.ArticleWithCategoryDTO;
import com.example.registration.dto.ArticleWithShareDTO;
import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.model.Article;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @GetMapping("/get_articles")
    public ResponseEntity<?> getArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String email,
            // 新增筛选参数（均为可选）
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        // 创建分页请求 (page从1开始计数)
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createTime").descending());

        // 调用Service获取分页数据
        Page<ArticleWithCategoryDTO> articlePage = articleService.getArticlesWithConditions(pageable, userId, tags, category, startDate, endDate);

        Map<String, Object> responseBody = Map.of(
                "total", articlePage.getTotalElements(),
                "data", articlePage.getContent()
        );
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/shared")
    public ResponseEntity<?> getSharedArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String email) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createTime").descending());
        // 调用Service获取分页数据
        Page<ArticleWithShareDTO> articlePage = articleService.getSharedArticles(pageable);
        Map<String, Object> responseBody = Map.of(
                "total", articlePage.getTotalElements(),
                "data", articlePage.getContent()
        );
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/article_editor")
    public ResponseEntity<?> getArticle(@RequestParam Long articleId) {
        Article article = articleService.getArticle(articleId);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "data", article
        ));
    }
    @PostMapping("/article_editor/modify")
    public ResponseEntity<?> modifyArticle(@RequestParam String email,
                                           @RequestParam Long articleId,
                                           @RequestParam Long lastCategoryId,
                                           @RequestBody ArticleCreateRequest request) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Article article = articleService.modifyArticle(userId, articleId, lastCategoryId,request);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "文章修改成功",
                "data", article
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticleById(id);
            
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文章不存在");
        }
    }
    @GetMapping("/article_view")
    public ResponseEntity<?> getArticleView(@RequestParam String email,
                                            @RequestParam Long articleId) {
        ArticleViewDTO article = articleService.getArticleView(email,articleId);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "data", article
        ));
    }

}
