package com.rumor.tracing.ai.service.impl;

import com.rumor.tracing.ai.dto.RumorAnalysisRequest;
import com.rumor.tracing.ai.dto.RumorAnalysisResponse;
import com.rumor.tracing.ai.dto.RumorAnalysisResponse.SourceAnalysis;
import com.rumor.tracing.ai.service.AIService;
import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.model.AnalysisType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeepSeekAIService implements AIService {
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    
    @Value("${ai.service.api-key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    
    public DeepSeekAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public RumorAnalysis analyzeText(String content) {
        String result = callDeepSeekAPI("text", content);
        return createAnalysis(AnalysisType.TEXT, result);
    }
    
    @Override
    public RumorAnalysis analyzeImage(String imageUrl) {
        String result = callDeepSeekAPI("image", imageUrl);
        return createAnalysis(AnalysisType.IMAGE, result);
    }
    
    @Override
    public RumorAnalysis analyzeVideo(String videoUrl) {
        String result = callDeepSeekAPI("video", videoUrl);
        return createAnalysis(AnalysisType.VIDEO, result);
    }
    
    @Override
    public RumorAnalysis analyzeMultiModal(String content, String imageUrl, String videoUrl) {
        String result = callDeepSeekAPI("multi", content, imageUrl, videoUrl);
        return createAnalysis(AnalysisType.MULTI, result);
    }
    
    @Override
    public RumorAnalysisResponse analyzeRumor(RumorAnalysisRequest request) {
        String result = callDeepSeekAPI("rumor", request.getTitle(), request.getContent());
        return RumorAnalysisResponse.builder()
                .credibilityScore(calculateConfidence(result))
                .factCheckingPoints(List.of("Fact 1", "Fact 2"))
                .misinformationIndicators(List.of("Indicator 1", "Indicator 2"))
                .verificationRecommendation("Recommendation")
                .sourceAnalysis(new SourceAnalysis() {{
                    setReliability(0.8);
                    setReputation("reliable");
                    setConcerns(List.of());
                }})
                .build();
    }
    
    private RumorAnalysis createAnalysis(AnalysisType type, String result) {
        RumorAnalysis analysis = new RumorAnalysis();
        analysis.setType(type);
        analysis.setResult(result);
        analysis.setConfidence(calculateConfidence(result));
        analysis.setAnalyzedAt(LocalDateTime.now());
        return analysis;
    }
    
    private String callDeepSeekAPI(String type, String... inputs) {
        // TODO: Implement DeepSeek API call
        return "Analysis result for " + type;
    }
    
    private Double calculateConfidence(String result) {
        // TODO: Implement confidence calculation
        return 0.8;
    }
} 