package com.rumor.tracing.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RumorAnalysisResponse {
    private boolean isRumor;
    private double confidence;
    private String explanation;
    private SourceAnalysis sourceAnalysis;
    private double credibilityScore;
    private List<String> factCheckingPoints;
    private List<String> misinformationIndicators;
    private String verificationRecommendation;

    @Data
    public static class SourceAnalysis {
        private double reliability;
        private String reputation;
        private List<String> concerns;
    }
} 