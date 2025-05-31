package com.example.registration.service;

import com.example.registration.dto.CommentDTO;
import com.example.registration.dto.CommentRequest;
import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.model.Comment;
import com.example.registration.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleIdOrderByCreateTimeAsc(articleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createComment(CommentRequest request,String author,String email) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setEmail(email);
        comment.setArticleId(request.getArticleId());
        comment = commentRepository.save(comment);
        return convertToDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found or permission denied"));

        commentRepository.delete(comment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor());
        dto.setEmail(comment.getEmail());
        LocalDateTime createTime = comment.getCreateTime();
        dto.setDate(createTime != null ?
                createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null);
        return dto;
    }
}