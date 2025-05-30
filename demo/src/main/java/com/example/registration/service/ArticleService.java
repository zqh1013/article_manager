package com.example.registration.service;

import com.example.registration.dto.ArticleCreateRequest;
import com.example.registration.dto.ArticleWithCategoryDTO;
import com.example.registration.exception.exception.EmailNotRegisteredException;
import com.example.registration.model.Article;
import com.example.registration.model.Category;
import com.example.registration.repository.ArticleRepository;
import com.example.registration.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Article createArticle(Long userId, ArticleCreateRequest request) {

        List<String> processedTags = request.getTags().stream()
                .map(String::trim)                  // 去除空格
                .filter(tag -> !tag.isEmpty())      // 过滤空标签
                .distinct()                         // 去重
                .toList();


        Article article = new Article();
        article.setTitle(request.getTitle().trim());
        article.setCategoryId(request.getCategoryId());
        article.setTags(processedTags);
        article.setContent(request.getContent());
        article.setUserId(userId);
        //article.setStatus(request.getIsDraft() ? ArticleStatus.DRAFT : ArticleStatus.PUBLISHED);
        article.setVisibility(request.getVisibility());
//        Category category = categoryRepository.findById(request.getCategoryId());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setArticleCount(category.getArticleCount() + 1);
        categoryRepository.save(category);
        return articleRepository.save(article);
    }

    // 新增分页查询方法
//    public Page<Article> getArticles(Pageable pageable, String title, String author) {
//        // 示例实现 - 根据你的实际数据访问层调整
//        if (title != null && author != null) {
//            return articleRepository.findByTitleContainingAndAuthorContaining(title, author, pageable);
//        } else if (title != null) {
//            return articleRepository.findByTitleContaining(title, pageable);
//        } else if (author != null) {
//            return articleRepository.findByAuthorContaining(author, pageable);
//        } else {
//            return articleRepository.findAll(pageable);
//        }
//    }
    public Page<ArticleWithCategoryDTO> getArticles(Pageable pageable, Long userId) {
        Page<ArticleWithCategoryDTO> pages =   articleRepository.findArticlesWithCategoryByUserId(userId, pageable);
        return pages;
    }

    public Article getArticle(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "文章不存在，ID: " + articleId));;
        return article;
    }


    public Article modifyArticle(Long userId, Long articleId, Long lastCategoryId, ArticleCreateRequest request){
        List<String> processedTags = request.getTags().stream()
                .map(String::trim)                  // 去除空格
                .filter(tag -> !tag.isEmpty())      // 过滤空标签
                .distinct()                         // 去重
                .toList();

        Article article = new Article();
        article.setId(articleId);
        article.setTitle(request.getTitle().trim());
        article.setCategoryId(request.getCategoryId());
        article.setTags(processedTags);
        article.setContent(request.getContent());
        article.setUserId(userId);
        article.setVisibility(request.getVisibility());
        if(lastCategoryId!=request.getCategoryId()) {
            Category lastCategory = categoryRepository.findById(lastCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            lastCategory.setArticleCount(lastCategory.getArticleCount() - 1);

            Category nowCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            nowCategory.setArticleCount(nowCategory.getArticleCount() + 1);
            categoryRepository.save(lastCategory);
            categoryRepository.save(nowCategory);
        }
        return articleRepository.save(article);
    }
}
