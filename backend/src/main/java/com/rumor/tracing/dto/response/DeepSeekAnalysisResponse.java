package com.rumor.tracing.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import com.rumor.tracing.dto.DeepSeekSourceAnalysis;    

@Data
@Builder
public class DeepSeekAnalysisResponse {
    private Double credibilityScore;
    private List<String> factCheckingPoints;
    private List<String> misinformationIndicators;
    private String verificationRecommendation;
    private DeepSeekSourceAnalysis sourceAnalysis;
}
