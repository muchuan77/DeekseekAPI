package com.rumor.tracing.dto.request;

import lombok.Data;

@Data
public class DeepSeekAnalysisRequest {
    private String content;
    private String source;
    private String type;
    private String title;
    private String imageUrl;
    private String videoUrl;
}
