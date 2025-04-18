package com.rumor.tracing.controller;

import com.rumor.tracing.dto.request.DeepSeekAnalysisRequest;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.service.DeepSeekAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/deepseek")
@RequiredArgsConstructor
@Tag(name = "DeepSeek分析", description = "DeepSeek AI分析相关接口")
public class DeepSeekAnalysisController {
    
    private final DeepSeekAIService deepSeekAIService;
    
    @PostMapping(value = "/analyze/text", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "分析文本内容")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeText(@RequestBody DeepSeekAnalysisRequest request) {
        return ResponseEntity.ok()
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .body(ApiResponse.success(deepSeekAIService.analyzeText(request.getContent())));
    }
    
    @PostMapping(value = "/analyze/image", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "分析图片内容")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeImage(@RequestBody DeepSeekAnalysisRequest request) {
        return ResponseEntity.ok()
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .body(ApiResponse.success(deepSeekAIService.analyzeImage(request.getContent())));
    }
    
    @PostMapping(value = "/analyze/video", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "分析视频内容")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeVideo(@RequestBody DeepSeekAnalysisRequest request) {
        return ResponseEntity.ok()
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .body(ApiResponse.success(deepSeekAIService.analyzeVideo(request.getContent())));
    }
    
    @PostMapping(value = "/analyze/multimodal", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "多模态分析")
    public ResponseEntity<ApiResponse<RumorAnalysis>> analyzeMultiModal(
            @RequestBody DeepSeekAnalysisRequest request) {
        return ResponseEntity.ok()
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .body(ApiResponse.success(
                deepSeekAIService.analyzeMultiModal(
                    request.getContent(),
                    request.getImageUrl(),
                    request.getVideoUrl()
                )
            ));
    }
} 