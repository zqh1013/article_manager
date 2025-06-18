package com.example.registration.service;

import com.example.registration.dto.ArticleCreateRequest;
import com.example.registration.dto.ArticleWithCategoryDTO;
import com.example.registration.exception.exception.EmailNotRegisteredException;
import com.example.registration.dto.*;
import com.example.registration.model.Article;
import com.example.registration.model.Category;
import com.example.registration.model.Comment;
import com.example.registration.repository.ArticleRepository;
import com.example.registration.repository.CategoryRepository;
import com.example.registration.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

//    public ArticleService(ArticleRepository articleRepository,CategoryRepository categoryRepository) {
//        this.articleRepository = articleRepository;
//        this.categoryRepository=categoryRepository;
//    }

//    public ArticleService(ArticleRepository articleRepository,CategoryRepository categoryRepository) {
//        this.articleRepository = articleRepository;
//        this.categoryRepository=categoryRepository;
//    }

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

    @Transactional
    public void deleteArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "文章不存在，ID: " + id));
        Category category = categoryRepository.findById(article.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setArticleCount(category.getArticleCount() - 1);
        categoryRepository.save(category);
        articleRepository.deleteById(id);
    }


    // 新增分页查询方法
//    public Page<ArticleWithCategoryDTO> getArticles(Pageable pageable, Long userId) {
//        Page<ArticleWithCategoryDTO> pages =   articleRepository.findArticlesWithCategoryByUserId(userId, pageable);
//        return pages;
//    }

    public Page<ArticleWithShareDTO> getSharedArticles(Pageable pageable) {
        Page<ArticleWithShareDTO> pages = articleRepository.findPublicArticles(pageable);
        return pages;
    }

    public Article getArticle(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "文章不存在，ID: " + articleId));;
        return article;
    }


    @Transactional
    public Page<ArticleWithCategoryDTO> getArticlesWithConditions(
        Pageable pageable, Long userId, String tags,
        Long category, LocalDate startDate, LocalDate endDate) {

        // 转换参数格式
        String tagsJson = null;
        Set<String> tagSet = new HashSet<>(); // 用于存储标签的Set集合
        if (StringUtils.hasText(tags)) {
            List<String> tagList = Arrays.asList(tags.split(","));
            tagSet = new HashSet<>(tagList); // 转换为Set便于高效查找
            tagsJson = "[\"" + String.join("\",\"", tagList) + "\"]"; // 格式示例: ["tag1","tag2"]
        }
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        // 查询
//        Page<ArticleWithCategoryDTO> pages = articleRepository.findArticlesWithConditions(
//                        userId, tagsJson, category,    // tags和tagList都传给repository层
//                        startDateTime, endDateTime, pageable);
//
//        // 在Service层进行tags过滤
//        Set<String> finalTagSet = tagSet;
//        List<ArticleWithCategoryDTO> filteredContent = pages.getContent().stream()
//                .filter(dto -> hasOverlap(dto.getTags(), finalTagSet))
//                .collect(Collectors.toList());
//
//        // 创建新的分页结果（保持分页信息不变）
//        return new PageImpl<>(filteredContent, pageable, filteredContent.size());

//        // 获取未过滤的原始分页结果
//        Page<ArticleWithCategoryDTO> pages = articleRepository.findArticlesWithConditions(
//                userId, tagsJson, category,
//                startDateTime, endDateTime, pageable);
//
//        // 在Service层进行tags过滤（保持原始分页信息）
//        Set<String> finalTagSet = tagSet;
//        List<ArticleWithCategoryDTO> filteredContent = pages.getContent().stream()
//                .filter(dto -> hasOverlap(dto.getTags(), finalTagSet))
//                .collect(Collectors.toList());
//
//        // *** 修复关键：创建新分页时使用原始分页信息 ***
//        return new PageImpl<>(
//                filteredContent,                 // 过滤后的内容
//                pages.getPageable(),             // 原始分页请求
//                pages.getTotalElements()         // 维护过滤前的总记录数（重要！）
//        );
        // 关键修改：获取所有数据（不分页）
        List<ArticleWithCategoryDTO> allItems = articleRepository.findArticlesWithConditions(
                userId, tagsJson, category, startDateTime, endDateTime);

        // 应用标签过滤（对完整数据集）
        Set<String> finalTagSet = tagSet;
        List<ArticleWithCategoryDTO> filteredItems = allItems.stream()
                .filter(dto -> hasOverlap(dto.getTags(), finalTagSet))
                .collect(Collectors.toList());

        // 手动分页：基于完整过滤后的结果集
        int totalItems = filteredItems.size();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        // 计算当前页数据范围
        List<ArticleWithCategoryDTO> pageContent;
        if (startItem < totalItems) {
            int toIndex = Math.min(startItem + pageSize, totalItems);
            pageContent = filteredItems.subList(startItem, toIndex);
        } else {
            pageContent = Collections.emptyList();
        }

        // 创建正确分页信息（包括实际总元素数）
        return new PageImpl<>(
                pageContent,
                PageRequest.of(currentPage, pageSize),  // 使用新的分页请求
                totalItems
        );

    }
    // 检查DTO的tags集合与输入tags集合是否有交集
    private boolean hasOverlap(List<String> dtoTags, Set<String> inputTagSet) {
        // 如果没有输入tags，则不进行过滤
        if (inputTagSet == null || inputTagSet.isEmpty()) {
            return true;
        }
        // 如果DTO没有tags，直接过滤掉
        if (dtoTags == null || dtoTags.isEmpty()) {
            return false;
        }
        // 检查是否有交集
        for (String tag : dtoTags) {
            if (inputTagSet.contains(tag)) {
                return true;
            }
        }
        return false;
    }


    @Transactional
    public Page<ArticleWithCategoryDTO> getArticlesWithText(
            Pageable pageable, Long userId, String text) {
        // 预处理搜索文本
        String process_text = preprocessSearchText(text);

        // 查询获取分页结果
        Page<ArticleWithSearchDTO> dtos = articleRepository.findArticlesByContent(
                userId, process_text, pageable
        );

        // 使用map转换DTO（推荐方案）
        Page<ArticleWithCategoryDTO> cdtos = dtos.map(dto ->
                new ArticleWithCategoryDTO(
                        dto.getId(),
                        dto.getTitle(),
                        dto.getCategoryId(),  // 匹配category_id字段
                        dto.getVisibility(),
                        // 格式化时间（处理空值）
                        dto.getCreateTime() != null ?
                                dto.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null,
                        dto.getCategoryName()  // 匹配category_name
                )
        );

        return cdtos;
    }
    private String preprocessSearchText(String text) {
        // 1. 移除特殊字符（保留中文和基本符号）
        String cleanText = text.replaceAll("[^\\p{L}\\p{Nd}\\u4E00-\\u9FA5]+", " ");

        // 2. 按 ngram 分词规则拆分并添加布尔操作符
        return Arrays.stream(cleanText.split("\\s+"))
                .filter(word -> word.length() >= 2) // ngram 最小词长为2
                .map(word -> "+" + word)            // 必须包含（布尔模式）
                .collect(Collectors.joining(" "));
    }

    @Transactional
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

    public ArticleViewDTO getArticleView(String email,Long articleId){
        Article article = getArticle(articleId);
        ArticleViewDTO dto = convertToDTO(article);
        String name = userRepository.findNicknameById(article.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("没有找到作者"));
        String user_email = userRepository.findEmailById(article.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("没有找到作者邮箱"));
        if(email != null && email.equals(user_email)){
            String categoryName = categoryRepository.findNameById(article.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("没有找到分类"));
            dto.setCategoryName(categoryName);
        }
        dto.setAuthor(name);
        dto.setAiSummary(null);
        return dto;
    }

    private ArticleViewDTO convertToDTO(Article article) {
        ArticleViewDTO dto = new ArticleViewDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        LocalDateTime createTime = article.getCreateTime();
        dto.setCreateTime(createTime != null ?
                createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null);
        return dto;
    }
}
