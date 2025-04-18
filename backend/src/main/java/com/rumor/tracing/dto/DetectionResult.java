package com.rumor.tracing.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DetectionResult {
    private String id;
    private String content;
    private String source;
    private String metadata;
    private String veracity;
    private Double confidence;
    private String analysis;
    private List<String> evidence;
    private LocalDateTime createdAt;
} 