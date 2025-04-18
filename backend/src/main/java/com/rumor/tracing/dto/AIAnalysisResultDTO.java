package com.rumor.tracing.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AIAnalysisResultDTO {
    private String id;
    private String type;  // TEXT, IMAGE, VIDEO
    private String status;  // PENDING, PROCESSING, COMPLETED, FAILED
    private Double confidenceScore;
    private Map<String, Object> results;  // Flexible structure to store different types of analysis results
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String userId;
    private String modelVersion;
} 