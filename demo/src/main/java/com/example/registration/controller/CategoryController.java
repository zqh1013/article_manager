package com.example.registration.controller;

import com.example.registration.dto.CategoryDTO;
import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.model.Category;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService,UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    // 获取分类树形结构
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(@RequestParam String email) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return ResponseEntity.ok(categoryService.getCategoryTree(userId));
    }

    // 创建分类
    @PostMapping("/add")
    public ResponseEntity<Category> createCategory(@RequestParam String email,
                                                   @RequestBody Category category) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(category,userId));
    }
    @PostMapping("/modify")
    public ResponseEntity<Category> modifyCategory(@RequestParam String email,
                                                   @RequestBody Category category) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.modifyCategory(category,userId));
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@RequestParam String email,
                                               @PathVariable Long id) {
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        categoryService.deleteCategory(id,userId);
        return ResponseEntity.noContent().build();
    }

    // 获取分类选项（用于父分类选择）
    //    @GetMapping("/options")
    //    public ResponseEntity<List<TreeCategoryDTO>> getCategoryOptions() {
    //        return ResponseEntity.ok(categoryService.getCategoryOptions());
    //    }
}