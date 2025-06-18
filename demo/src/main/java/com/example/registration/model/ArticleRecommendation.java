package com.example.registration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRecommendation {
    private Long articleId;
    private String title;
    private double similarity;

    public ArticleRecommendation() {}

    public ArticleRecommendation(Long articleId, String title, double similarity) {
        this.articleId = articleId;
        this.title = title;
        this.similarity = similarity;
    }

}
