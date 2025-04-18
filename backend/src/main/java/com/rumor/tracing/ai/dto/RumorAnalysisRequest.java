package com.rumor.tracing.ai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RumorAnalysisRequest {
    private String title;
    private String content;
    private String source;
} 