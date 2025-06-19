package com.example.registration.service;

import com.example.registration.dto.ArticleViewDTO;
import com.example.registration.dto.CommentDTO;
import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.model.Article;
import com.example.registration.model.Comment;
import com.example.registration.repository.ArticleRepository;
import com.example.registration.repository.CommentRepository;
import com.example.registration.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public AdminService(ArticleRepository articleRepository,
                        CommentRepository commentRepository,
                        UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // 审核单篇文章
    @Transactional
    public void reviewArticle(Long articleId, Article.ReviewStatus status) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        article.setReviewStatus(status);
        articleRepository.save(article);
    }

    // 一键通过所有待审核文章
    @Transactional
    public void approveAllPendingArticles() {
        articleRepository.findAllPublicByReviewStatus(Article.ReviewStatus.PENDING)
                .forEach(article -> {
                    article.setReviewStatus(Article.ReviewStatus.APPROVED);
                    articleRepository.save(article);
                });
    }

    // 删除评论
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    // 获取待审核文章（分页）返回 ArticleViewDTO
    public Page<ArticleViewDTO> getPendingArticles(Pageable pageable) {

        Page<Article> articlePage = articleRepository.findPublicByReviewStatus(Article.ReviewStatus.PENDING,pageable);

        // 转换为 ArticleViewDTO
        List<ArticleViewDTO> dtos = articlePage.getContent().stream()
                .map(this::convertToArticleViewDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, articlePage.getTotalElements());
    }

    private ArticleViewDTO convertToArticleViewDTO(Article article) {
        ArticleViewDTO dto = new ArticleViewDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());

        // 获取作者昵称
        String author = userRepository.findNicknameById(article.getUserId())
                .orElse("未知作者");
        dto.setAuthor(author);

        // 格式化创建时间
        if (article.getCreateTime() != null) {
            dto.setCreateTime(article.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        return dto;
    }

    // 获取待管理评论（分页）返回 CommentDTO
    public Page<CommentDTO> getRecentComments(Pageable pageable) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        Page<Comment> commentPage = commentRepository.findApprovedPublicComments(Article.ReviewStatus.PENDING,startTime, pageable);

        // 转换为 CommentDTO
        List<CommentDTO> dtos = commentPage.getContent().stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, commentPage.getTotalElements());
    }

    private CommentDTO convertToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor());

        // 格式化创建时间
        if (comment.getCreateTime() != null) {
            dto.setDate(comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        // 获取关联文章标题
        String articleTitle = articleRepository.findById(comment.getArticleId())
                .map(Article::getTitle)
                .orElse("未知文章");
        dto.setArticleTitle(articleTitle);
        dto.setArticleId(comment.getArticleId());
        return dto;
    }
}