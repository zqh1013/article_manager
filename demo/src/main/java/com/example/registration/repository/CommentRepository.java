package com.example.registration.repository;

import com.example.registration.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdOrderByCreateTimeAsc(Long articleId);

    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.email = :email")
    Optional<Comment> findByIdAndEmail(@Param("id") Long id, @Param("email") String email);

    Optional<Comment> findById(Long id);
}
