package com.rumor.tracing.controller;

import com.rumor.tracing.dto.request.DeepSeekAnalysisRequest;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.service.DeepSeekAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deepseek")
@RequiredArgsConstructor
@Tag(name = "DeepSeek分析", description = "DeepSeek AI分析相关接口")
public class DeepSeekAnalysisController {
    
    private final DeepSeekAIService deepSeekAIService;
    
    @PostMapping("/analyze/text")
    @Operation(summary = "分析文本内容")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeText(@RequestBody String content) {
        return ResponseEntity.ok(ApiResponse.success(deepSeekAIService.analyzeText(content)));
    }
    
    @PostMapping("/analyze/image")
    @Operation(summary = "分析图片内容")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeImage(@RequestBody String imageUrl) {
        return ResponseEntity.ok(ApiResponse.success(deepSeekAIService.analyzeImage(imageUrl)));
    }
    
    @PostMapping("/analyze/video")
    @Operation(summary = "分析视频内容")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeVideo(@RequestBody String videoUrl) {
        return ResponseEntity.ok(ApiResponse.success(deepSeekAIService.analyzeVideo(videoUrl)));
    }
    
    @PostMapping("/analyze/multimodal")
    @Operation(summary = "多模态分析")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeMultiModal(
            @RequestBody DeepSeekAnalysisRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            deepSeekAIService.analyzeMultiModal(
                request.getContent(),
                request.getImageUrl(),
                request.getVideoUrl()
            )
        ));
    }
} 