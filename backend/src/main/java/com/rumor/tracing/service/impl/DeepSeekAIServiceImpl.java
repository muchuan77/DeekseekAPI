// DeepSeekAIServiceImpl.java
package com.rumor.tracing.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumor.tracing.dto.request.DeepSeekAnalysisRequest;
import com.rumor.tracing.dto.response.DeepSeekAnalysisResponse;
import com.rumor.tracing.dto.DeepSeekSourceAnalysis;
import com.rumor.tracing.entity.DeepSeekAnalysis;
import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.model.AnalysisType;
import com.rumor.tracing.repository.DeepSeekAnalysisRepository;
import com.rumor.tracing.service.DeepSeekAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekAIServiceImpl implements DeepSeekAIService {
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    
    @Value("${ai.service.api-key}")
    private String apiKey;
    
    @Value("${ai.service.model}")
    private String model;

    @Value("${ai.service.temperature}")
    private double temperature;

    @Value("${ai.service.max-tokens}")
    private int maxTokens;

    @Value("${ai.service.timeout}")
    private int timeout;

    @Value("${ai.service.max-retries}")
    private int maxRetries;

    @Value("${ai.service.retry-delay}")
    private int retryDelay;

    @Value("${ai.service.frequency-penalty}")
    private double frequencyPenalty;

    @Value("${ai.service.presence-penalty}")
    private double presencePenalty;

    @Value("${ai.service.top-p}")
    private double topP;
    
    private final RestTemplate restTemplate;
    private final DeepSeekAnalysisRepository deepSeekAnalysisRepository;
    
    @PostConstruct
    public void init() {
        // 配置 RestTemplate 超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 增加到 30 秒
        factory.setReadTimeout(30000);    // 增加到 30 秒
        restTemplate.setRequestFactory(factory);
        
        // 配置消息转换器
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        converters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(converters);
    }
    
    @Override
    public RumorAnalysis analyzeText(String content) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(content);
        request.setType("TEXT");
        return analyzeContent(request);
    }
    
    @Override
    public RumorAnalysis analyzeImage(String imageUrl) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(imageUrl);
        request.setType("image");
        return analyzeContent(request);
    }
    
    @Override
    public RumorAnalysis analyzeVideo(String videoUrl) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(videoUrl);
        request.setType("video");
        return analyzeContent(request);
    }
    
    @Override
    public RumorAnalysis analyzeMultiModal(String content, String imageUrl, String videoUrl) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(content);
        request.setImageUrl(imageUrl);
        request.setVideoUrl(videoUrl);
        request.setType("multimodal");
        return analyzeContent(request);
    }
    
    @Override
    public DeepSeekAnalysisResponse analyzeRumor(DeepSeekAnalysisRequest request) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "你是一个专业的谣言分析助手，请分析以下内容并给出详细的分析报告。"),
                Map.of("role", "user", "content", request.getContent())
            ));
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("frequency_penalty", frequencyPenalty);
            requestBody.put("presence_penalty", presencePenalty);
            requestBody.put("top_p", topP);
            requestBody.put("stream", false);

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            log.info("Sending request to DeepSeek API: {}", requestBody);
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/chat/completions",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            log.info("Received response from DeepSeek API: {}", response.getBody());

            // 解析响应
            return parseDeepSeekResponse(response.getBody());
        } catch (Exception e) {
            log.error("Failed to analyze content", e);
            throw new RuntimeException("Failed to analyze content", e);
        }
    }
    
    private RumorAnalysis analyzeContent(DeepSeekAnalysisRequest request) {
        DeepSeekAnalysisResponse response = analyzeRumor(request);
        
        // 保存分析结果
        DeepSeekAnalysis analysis = new DeepSeekAnalysis();
        analysis.setAnalysisType(request.getType());
        analysis.setResult(response.getVerificationRecommendation());
        analysis.setConfidence(response.getCredibilityScore());
        analysis = deepSeekAnalysisRepository.save(analysis);
        
        // 转换为RumorAnalysis
        RumorAnalysis result = new RumorAnalysis();
        result.setId(analysis.getId());
        result.setType(AnalysisType.valueOf(analysis.getAnalysisType()));
        result.setResult(analysis.getResult());
        result.setConfidence(analysis.getConfidence());
        result.setAnalyzedAt(analysis.getCreatedAt());
        
        return result;
    }
    
    private DeepSeekAnalysisResponse parseDeepSeekResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            
            // 获取 AI 的回复内容
            String aiResponse = root.path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();
            
            // 解析 Markdown 内容
            return parseMarkdownResponse(aiResponse);
        } catch (Exception e) {
            log.error("Failed to parse DeepSeek response", e);
            throw new RuntimeException("Failed to parse DeepSeek response", e);
        }
    }
    
    private DeepSeekAnalysisResponse parseMarkdownResponse(String markdown) {
        // 初始化默认值
        double credibilityScore = 0.5;
        String verificationRecommendation = "需要进一步验证";
        
        // 解析 Markdown 内容
        String[] lines = markdown.split("\n");
        for (String line : lines) {
            if (line.contains("可信度评分")) {
                // 提取可信度评分
                String scoreStr = line.replaceAll("[^0-9.]", "");
                try {
                    credibilityScore = Double.parseDouble(scoreStr);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse credibility score: {}", scoreStr);
                }
            } else if (line.contains("验证建议")) {
                // 提取验证建议
                verificationRecommendation = line.substring(line.indexOf("：") + 1).trim();
            }
        }
        
        return DeepSeekAnalysisResponse.builder()
            .credibilityScore(credibilityScore)
            .verificationRecommendation(verificationRecommendation)
            .build();
    }
    
    private List<String> parseList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                list.add(item.asText());
            }
        }
        return list;
    }
    
    private DeepSeekSourceAnalysis parseSourceAnalysis(JsonNode node) {
        DeepSeekSourceAnalysis sourceAnalysis = new DeepSeekSourceAnalysis();
        if (!node.isMissingNode()) {
            sourceAnalysis.setReliability(node.path("reliability").asDouble(0.0));
            sourceAnalysis.setReputation(node.path("reputation").asText(""));
            sourceAnalysis.setConcerns(parseList(node.path("concerns")));
        }
        return sourceAnalysis;
    }
}