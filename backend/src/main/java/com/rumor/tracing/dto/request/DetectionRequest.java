package com.rumor.tracing.dto.request;

import lombok.Data;

@Data
public class DetectionRequest {
    private String content;
    private String source;
    private String metadata;
} 