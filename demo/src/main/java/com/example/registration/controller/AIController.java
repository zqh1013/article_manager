package com.example.registration.controller;

import com.example.registration.exception.exception.ResourceNotFoundException;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.AIService;
import com.example.registration.service.ArticleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService aiService;
    private final RestTemplate restTemplate = new RestTemplate();

    private final UserRepository userRepository;


    public AIController(UserRepository userRepository, AIService aiService) {
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

    @Value("${baidu.api.authorization}")
    private String authorization;

    @Value("${baidu.api.endpoint}")
    private String apiEndpoint;

    @PostMapping("/generate-summary")
    public ResponseEntity<Map<String, Object>> generateSummary(@RequestBody Map<String, String> request) {
        // 处理请求参数
        String content = request.get("content");
        String model = request.getOrDefault("model", "ernie-3.5");

        // 验证内容
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "文章内容不能为空")
            );
        }

        try {
            // 验证授权凭证
            if (authorization == null || authorization.trim().isEmpty()) {
                return ResponseEntity.internalServerError().body(
                        Collections.singletonMap("error", "未配置授权凭证")
                );
            }

            // 准备AI请求负载
            Map<String, Object> aiPayload = new HashMap<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", "请为以下文章生成100字以内的中文摘要：\n\n" + content);

            aiPayload.put("messages", Collections.singletonList(userMessage));
            aiPayload.put("temperature", 0.3);
            aiPayload.put("max_output_tokens", 200);
            aiPayload.put("model", model);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("Authorization", authorization); // 直接使用Authorization头

            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(aiPayload, headers);
            Map<String, Object> apiResponse = restTemplate.postForObject(
                    apiEndpoint, entity, Map.class
            );

            // 验证AI响应
            if (apiResponse == null || !apiResponse.containsKey("result")) {
                return ResponseEntity.internalServerError().body(
                        Collections.singletonMap("error", "AI服务响应异常")
                );
            }

            // 提取结果并构建响应
            Map<String, Object> result = new HashMap<>();
            result.put("summary", apiResponse.get("result"));
            result.put("model", model);

            if (apiResponse.containsKey("usage")) {
                Map<String, Object> usage = (Map<String, Object>) apiResponse.get("usage");
                if (usage != null && usage.containsKey("total_tokens")) {
                    result.put("tokens", usage.get("total_tokens"));
                }
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Collections.singletonMap("error", "处理请求失败：" + e.getMessage())
            );
        }
    }

    @GetMapping("/statistic")
    public  ResponseEntity<Map<String, Object>> generateStatistic(@RequestParam String email){
        Long userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Map<String, Object> stats = aiService.getCurrentMonthStats(userId);
        return ResponseEntity.ok(stats);
    }
}

