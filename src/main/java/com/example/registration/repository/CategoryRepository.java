package com.example.registration.repository;

import com.example.registration.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 查询所有分类（平铺结构）
    @Query("SELECT c FROM Category c ORDER BY c.parentId ASC")
    List<Category> findAllWithOrder();

    // 根据父ID查询子分类
    List<Category> findByParentId(Long parentId);

    Optional<Category> findById(Long Id);

    // 检查同一父节点下是否存在同名分类（排除自身）
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Category c WHERE c.parentId = :parentId AND c.name = :name AND c.id <> :excludeId")
    boolean existsByParentIdAndNameExcludingId(
            @Param("parentId") Long parentId,
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );
}