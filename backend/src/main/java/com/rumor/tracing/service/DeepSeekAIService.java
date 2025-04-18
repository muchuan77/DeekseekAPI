package com.rumor.tracing.service;

import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.dto.request.DeepSeekAnalysisRequest;
import com.rumor.tracing.dto.response.DeepSeekAnalysisResponse;

public interface DeepSeekAIService {
    
    /**
     * 分析文本内容
     * @param content 文本内容
     * @return 分析结果
     */
    RumorAnalysis analyzeText(String content);
    
    /**
     * 分析图片内容
     * @param imageUrl 图片URL
     * @return 分析结果
     */
    RumorAnalysis analyzeImage(String imageUrl);
    
    /**
     * 分析视频内容
     * @param videoUrl 视频URL
     * @return 分析结果
     */
    RumorAnalysis analyzeVideo(String videoUrl);
    
    /**
     * 多模态分析
     * @param content 文本内容
     * @param imageUrl 图片URL
     * @param videoUrl 视频URL
     * @return 分析结果
     */
    RumorAnalysis analyzeMultiModal(String content, String imageUrl, String videoUrl);

    DeepSeekAnalysisResponse analyzeRumor(DeepSeekAnalysisRequest request);
} 