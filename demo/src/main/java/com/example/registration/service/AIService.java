package com.example.registration.service;

import com.example.registration.model.Article;
import com.example.registration.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {
    private final ArticleRepository articleRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${baidu.api.authorization}")
    private String aiAuthorization;

    @Value("${baidu.api.endpoint}")
    private String aiEndpoint;

    public AIService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    public Map<String, Object> getCurrentMonthStats(Long userId){
        Map<String, Object> result = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        String month = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        result.put("month", month);

        //获取当前月份的文章
        List<Article> articles = articleRepository.findByUserIdAndMonth(
                userId,
                currentMonth.atDay(1).atStartOfDay(),
                currentMonth.atEndOfMonth().atTime(23, 59, 59)
        );
        int articleCount = articles.size();
        result.put("articleCount", articleCount);
        Map<String, Integer> keywordFrequency = new HashMap<>();
        articles.forEach(article -> {
            article.getTags().forEach(tag -> {
                keywordFrequency.put(tag, keywordFrequency.getOrDefault(tag, 0) + 1);
            });
        });

        List<Map<String, Object>> keywordCloud = keywordFrequency.entrySet().stream().limit(5)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> kw = new HashMap<>();
                    kw.put("keyword", entry.getKey());
                    kw.put("frequency", entry.getValue());
                    return kw;
                })
                .collect(Collectors.toList());

        result.put("keyword_cloud", keywordCloud);
        if (articleCount > 0) {
            Map<String, Object> aiReport = generateAIReport(month, articleCount, keywordCloud);
            result.put("ai_Report", aiReport);
        }

        result.put("status", "success");
        return result;
    }
    private Map<String, Object> generateAIReport(String month, int articleCount,
                                                 List<Map<String, Object>> keywordCloud) {
        try {
            // 提取关键词（取前5个）
            String keywords = keywordCloud.stream()
                    .map(kw -> String.format("%s(%d次)", kw.get("keyword"), kw.get("frequency")))
                    .collect(Collectors.joining(", "));

            // 准备AI提示词
            String aiPrompt = String.format(
                    "请基于以下用户知识库数据生成一段简短的月度总结（100字以内）：\n" +
                            "### 本月统计\n" +
                            "- 月份：%s\n" +
                            "- 文章数量：%d篇\n" +
                            "- 热门领域：%s\n\n" +
                            "### 总结要求\n" +
                            "1. 指出突出的关注领域\n" +
                            "2. 不超过100字",
                    month, articleCount, keywords
            );

            // 准备AI请求负载
            Map<String, Object> aiPayload = new HashMap<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", aiPrompt);

            aiPayload.put("messages", Collections.singletonList(userMessage));
            aiPayload.put("temperature", 0.7);
            aiPayload.put("max_output_tokens", 300);
            aiPayload.put("model", "ernie-3.5");

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", aiAuthorization);

            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(aiPayload, headers);
            Map<String, Object> apiResponse = restTemplate.postForObject(
                    aiEndpoint, entity, Map.class
            );

            // 处理响应
            if (apiResponse != null && apiResponse.containsKey("result")) {
                Map<String, Object> ReportInfo = new HashMap<>();
                ReportInfo.put("Report", apiResponse.get("result"));
                if (apiResponse.containsKey("usage")) {
                    Map<String, Object> usage = (Map<String, Object>) apiResponse.get("usage");
                    if (usage != null && usage.containsKey("total_tokens")) {
                        ReportInfo.put("tokens", usage.get("total_tokens"));
                    }
                }
                return ReportInfo;
            }

        } catch (Exception e) {
            // 记录日志
        }
        return Collections.singletonMap("error", "AI总结生成失败");
    }
}
