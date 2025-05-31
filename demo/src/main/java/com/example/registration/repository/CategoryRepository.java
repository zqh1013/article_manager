package com.example.registration.repository;

import com.example.registration.model.Category;
import com.example.registration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 按名称查询（增加用户隔离）
    @Query("SELECT c FROM Category c " +
            "WHERE LOWER(c.name) = LOWER(:name) " +
            "AND c.userId = :userId")  // 新增用户ID条件[6](@ref)
    Optional<Category> findByNameAndUserId(
            @Param("name") String name,
            @Param("userId") Long userId
    );

    // 查询当前用户的所有分类（平铺结构）
    @Query("SELECT c FROM Category c " +
            "WHERE c.userId = :userId " +  // 新增用户ID条件
            "ORDER BY c.parentId ASC")
    List<Category> findAllByUserId(@Param("userId") Long userId);  // 方法名和参数调整

    // 根据父ID查询子分类（增加用户隔离）
    @Query("SELECT c FROM Category c " +
            "WHERE c.parentId = :parentId " +
            "AND c.userId = :userId")  // 新增用户ID条件[8](@ref)
    List<Category> findByParentIdAndUserId(
            @Param("parentId") Long parentId,
            @Param("userId") Long userId
    );

    // 按ID查询（增加用户隔离）
    @Query("SELECT c FROM Category c " +
            "WHERE c.id = :id " +
            "AND c.userId = :userId")  // 新增用户ID条件[7](@ref)
    Optional<Category> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    // 检查同名分类（增加用户隔离）
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Category c " +
            "WHERE c.parentId = :parentId " +
            "AND LOWER(c.name) = LOWER(:name) " +
            "AND c.id <> :excludeId " +
            "AND c.userId = :userId")  // 新增用户ID条件[2](@ref)
    boolean existsByParentIdAndNameExcludingId(
            @Param("parentId") Long parentId,
            @Param("name") String name,
            @Param("excludeId") Long excludeId,
            @Param("userId") Long userId  // 新增参数
    );

    @Query("SELECT c.name FROM Category c " +
            "WHERE c.id = :id ")
    Optional<String> findNameById(
            @Param("id") Long id
    );
}