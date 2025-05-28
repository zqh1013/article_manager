package com.example.registration.controller;

import com.example.registration.dto.CategoryDTO;
import com.example.registration.model.Category;
import com.example.registration.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 获取分类树形结构
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    // 创建分类
    @PostMapping("/add")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(category));
    }
    @PostMapping("/modify")
    public ResponseEntity<Category> modifyCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.modifyCategory(category));
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // 获取分类选项（用于父分类选择）
    //    @GetMapping("/options")
    //    public ResponseEntity<List<TreeCategoryDTO>> getCategoryOptions() {
    //        return ResponseEntity.ok(categoryService.getCategoryOptions());
    //    }
}