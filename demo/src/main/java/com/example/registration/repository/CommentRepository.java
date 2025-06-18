package com.example.registration.repository;

import com.example.registration.model.Article;
import com.example.registration.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdOrderByCreateTimeAsc(Long articleId);

    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.email = :email")
    Optional<Comment> findByIdAndEmail(@Param("id") Long id, @Param("email") String email);

    Optional<Comment> findById(Long id);

//    @Query("""
//        SELECT c FROM Comment c
//        JOIN Article a ON c.articleId = a.id
//        WHERE c.reviewStatus = :reviewStatus
//        AND a.visibility = 'public'
//        AND a.reviewStatus = 'APPROVED'
//        ORDER BY c.createTime DESC
//    """)
//    Page<Comment> findApprovedPublicComments(
//            @Param("reviewStatus") Article.ReviewStatus reviewStatus,
//            Pageable pageable
//    );
    @Query("""
        SELECT c FROM Comment c
        JOIN Article a ON c.articleId = a.id
        WHERE c.reviewStatus = :reviewStatus
        AND a.visibility = 'public'
        AND a.reviewStatus = 'APPROVED'
        AND c.createTime >= :startTime
        ORDER BY c.createTime DESC
    """)
    Page<Comment> findApprovedPublicComments(
            @Param("reviewStatus") Article.ReviewStatus reviewStatus,
            @Param("startTime") LocalDateTime startTime,  // 新增参数
            Pageable pageable
    );
}
