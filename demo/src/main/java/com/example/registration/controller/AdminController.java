// 文档42: AdminController.java
package com.example.registration.controller;

import com.example.registration.dto.ArticleViewDTO;
import com.example.registration.model.Article;
import com.example.registration.model.Comment;
import com.example.registration.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 审核单篇文章
    @PutMapping("/articles/{id}/review")
    public ResponseEntity<?> reviewArticle(
            @PathVariable Long id,
            @RequestParam Article.ReviewStatus status) {
        adminService.reviewArticle(id, status);
        return ResponseEntity.ok().build();
    }

    // 一键通过所有待审核文章
    @PutMapping("/articles/approve-all")
    public ResponseEntity<?> approveAllPendingArticles() {
        adminService.approveAllPendingArticles();
        return ResponseEntity.ok().build();
    }

    // 删除评论
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteCommentAdmin(@PathVariable Long id) {
        adminService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // 获取待审核文章（分页）
    @GetMapping("/articles/pending")
    public ResponseEntity<?> getPendingArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime,desc") String sort) {

        // 解析排序参数
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<ArticleViewDTO> test= adminService.getPendingArticles(pageable);

        return ResponseEntity.ok(adminService.getPendingArticles(pageable));
    }

    // 获取待管理评论（分页）
    @GetMapping("/comments/recent")
    public ResponseEntity<?> getRecentComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime,desc") String sort) {

        // 解析排序参数
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

        return ResponseEntity.ok(adminService.getRecentComments(pageable));
    }
}