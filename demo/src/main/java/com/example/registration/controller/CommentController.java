package com.example.registration.controller;

import com.example.registration.dto.CommentDTO;
import com.example.registration.dto.CommentRequest;
import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    public CommentController(CommentService commentService,UserRepository userRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam Long articleId) {
        List<CommentDTO> dtos= commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "data", dtos
        ));
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestParam String email,
                                                    @RequestBody CommentRequest request) {
        String author = userRepository.findNicknameByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        CommentDTO dto= commentService.createComment(request,author,email);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "data", dto
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id
    ) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
