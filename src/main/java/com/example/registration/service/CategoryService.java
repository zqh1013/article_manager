package com.example.registration.service;

import com.example.registration.dto.CategoryDTO;
import com.example.registration.model.Category;
import com.example.registration.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //构建树
    public List<CategoryDTO> getCategoryTree() {
        List<Category> allCategories = categoryRepository.findAllWithOrder();
        return buildTree(null, allCategories);
    }

    private List<CategoryDTO> buildTree(Long parentId, List<Category> categories) {
        return categories.stream()
                .filter(c -> Objects.equals(c.getParentId(), parentId))
                .map(c -> {
                    CategoryDTO dto = new CategoryDTO();
                    BeanUtils.copyProperties(c, dto);
                    dto.setChildren(buildTree(c.getId(), categories));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId())
                    .orElseThrow(() -> new RuntimeException("父分类不存在"));
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        List<Long> idsToDelete = new ArrayList<>();
        findChildrenIds(id, idsToDelete);
        idsToDelete.add(id);
        categoryRepository.deleteAllById(idsToDelete);
    }

    private void findChildrenIds(Long parentId, List<Long> ids) {
        List<Category> children = categoryRepository.findByParentId(parentId);
        children.forEach(child -> {
            ids.add(child.getId());
            findChildrenIds(child.getId(), ids);
        });
    }

    @Transactional
    public Category modifyCategory(Category newCategory) {
        Long id;
        String newName;
        Long newParentId;
        id = newCategory.getId();
        newName = newCategory.getName();
        newParentId = newCategory.getParentId();

        // 参数基础校验
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 获取目标父ID（新父ID或原父ID）
        Long targetParentId = (newParentId != null) ? newParentId : category.getParentId();
        // 同一父节点下名称唯一性校验
        if (!newName.equals(category.getName()) ||
                (newParentId != null && !newParentId.equals(category.getParentId()))) {
            if (categoryRepository.existsByParentIdAndNameExcludingId(targetParentId, newName, id)) {
                throw new RuntimeException("同一父分类下名称必须唯一");
            }
        }

        // 父分类校验（参考网页7和网页8的校验逻辑）
        if (newParentId != null) {
            // 禁止设置自己为父分类
            if (id.equals(newParentId)) {
                throw new RuntimeException("不能设置自身为父分类");
            }

            // 校验父分类是否存在
            Category parent = categoryRepository.findById(newParentId)
                    .orElseThrow(() -> new RuntimeException("父分类不存在"));

            // 循环依赖校验（禁止设置子节点为父分类）
            if (isCircularReference(id, parent)) {
                throw new RuntimeException("父分类不能是当前分类的子节点");
            }
        }

        // 执行更新（参考网页8的更新方式）
        category.setName(newName);
        category.setParentId(newParentId);
        return categoryRepository.save(category);
    }

    // 循环依赖检测方法（深度优先遍历）
    private boolean isCircularReference(Long currentId, Category parent) {
        if (parent.getParentId() == null) return false;
        if (parent.getParentId().equals(currentId)) return true;
        return isCircularReference(currentId,
                categoryRepository.findById(parent.getParentId())
                        .orElseThrow(() -> new RuntimeException("父分类链异常"))
        );
    }

}
