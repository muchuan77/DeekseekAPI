package com.rumor.tracing.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import com.rumor.tracing.dto.DeepSeekSourceAnalysis;    

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekAnalysisResponse {
    @JsonProperty("credibilityScore")
    private Double credibilityScore;
    
    @JsonProperty("factCheckingPoints")
    private List<String> factCheckingPoints;
    
    @JsonProperty("misinformationIndicators")
    private List<String> misinformationIndicators;
    
    @JsonProperty("verificationRecommendation")
    private String verificationRecommendation;
    
    @JsonProperty("sourceAnalysis")
    private DeepSeekSourceAnalysis sourceAnalysis;
}
