package com.rumor.tracing.controller;

import com.rumor.tracing.dto.DetectionResult;
import com.rumor.tracing.dto.request.DetectionRequest;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.service.DetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detection")
@RequiredArgsConstructor
@Tag(name = "谣言检测", description = "谣言检测相关接口")
public class DetectionController {

    private final DetectionService detectionService;

    @PostMapping("/analyze")
    @Operation(summary = "分析谣言")
    public ResponseEntity<ApiResponse<DetectionResult>> analyzeRumor(@RequestBody DetectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(detectionService.analyzeRumor(request)));
    }

    @GetMapping("/history")
    @Operation(summary = "获取检测历史")
    public ResponseEntity<ApiResponse<List<DetectionResult>>> getDetectionHistory(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getDetectionHistory(startTime, endTime)));
    }

    @GetMapping("/results/{id}")
    @Operation(summary = "获取检测结果")
    public ResponseEntity<ApiResponse<DetectionResult>> getDetectionResult(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getDetectionResult(id)));
    }
} 