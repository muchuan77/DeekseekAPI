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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeepSeekAIServiceImpl implements DeepSeekAIService {
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    
    @Value("${ai.service.api-key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final DeepSeekAnalysisRepository deepSeekAnalysisRepository;
    
    @Override
    public RumorAnalysis analyzeText(String content) {
        DeepSeekAnalysisRequest request = new DeepSeekAnalysisRequest();
        request.setContent(content);
        request.setType("text");
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
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", request.getContent());
            requestBody.put("source", request.getSource());
            requestBody.put("type", request.getType());
            if (request.getTitle() != null) {
                requestBody.put("title", request.getTitle());
            }
            if (request.getImageUrl() != null) {
                requestBody.put("imageUrl", request.getImageUrl());
            }
            if (request.getVideoUrl() != null) {
                requestBody.put("videoUrl", request.getVideoUrl());
            }
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/analyze",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            // 解析响应
            return parseDeepSeekResponse(response.getBody());
        } catch (Exception e) {
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
            
            return DeepSeekAnalysisResponse.builder()
                .credibilityScore(root.path("credibility_score").asDouble())
                .factCheckingPoints(parseList(root.path("fact_checking_points")))
                .misinformationIndicators(parseList(root.path("misinformation_indicators")))
                .verificationRecommendation(root.path("verification_recommendation").asText())
                .sourceAnalysis(parseSourceAnalysis(root.path("source_analysis")))
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DeepSeek response", e);
        }
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
        sourceAnalysis.setReliability(node.path("reliability").asDouble());
        sourceAnalysis.setReputation(node.path("reputation").asText());
        sourceAnalysis.setConcerns(parseList(node.path("concerns")));
        return sourceAnalysis;
    }
}