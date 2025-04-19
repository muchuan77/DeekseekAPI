package com.rumor.tracing.controller;

import com.rumor.tracing.entity.PropagationPath;
import com.rumor.tracing.service.PropagationAnalysisService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/propagation")
@RequiredArgsConstructor
@Tag(name = "传播分析", description = "传播分析相关接口")
public class PropagationController {

    private final PropagationAnalysisService propagationAnalysisService;

    @GetMapping("/paths/{rumorId}")
    public ResponseEntity<Map<String, Object>> getPropagationPaths(
            @PathVariable Long rumorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            // 转换日期字符串为时间戳
            Long startTime = startDate != null ? 
                new SimpleDateFormat("yyyy-MM-dd").parse(startDate).getTime() : null;
            Long endTime = endDate != null ? 
                new SimpleDateFormat("yyyy-MM-dd").parse(endDate).getTime() : null;
            
            // 获取分页数据
            Pageable pageable = PageRequest.of(page, size);
            Page<PropagationPath> paths = propagationAnalysisService.getPropagationPaths(
                rumorId, startTime, endTime, pageable);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                    "content", paths.getContent(),
                    "totalElements", paths.getTotalElements(),
                    "totalPages", paths.getTotalPages()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取传播路径失败：" + e.getMessage()
            ));
        }
    }

    @GetMapping("/influence/{rumorId}")
    public ResponseEntity<Map<String, Object>> getInfluenceAnalysis(@PathVariable Long rumorId) {
        return ResponseEntity.ok(Map.of(
            "analysis", propagationAnalysisService.calculateInfluence(rumorId)
        ));
    }

    @GetMapping("/trends/{rumorId}")
    public ResponseEntity<Map<String, Object>> getPropagationTrends(
            @PathVariable Long rumorId,
            @RequestParam(defaultValue = "hour") String type) {
        try {
            List<Map<String, Object>> trends = propagationAnalysisService.analyzePropagationTrends(rumorId, type);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", trends
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取传播趋势失败：" + e.getMessage()
            ));
        }
    }

    @GetMapping("/key-nodes/{rumorId}")
    public ResponseEntity<Map<String, Object>> getKeyNodes(@PathVariable Long rumorId) {
        List<String> keyNodes = propagationAnalysisService.identifyKeyNodes(rumorId);
        return ResponseEntity.ok(Map.of(
            "keyNodes", keyNodes.stream().map(nodeId -> Map.of(
                "nodeId", nodeId,
                "influence", 0, // 这里需要计算实际影响力
                "pathCount", 0  // 这里需要计算实际路径数
            )).toList()
        ));
    }

    @GetMapping("/statistics/{rumorId}")
    public ResponseEntity<Map<String, Object>> getPropagationStatistics(@PathVariable Long rumorId) {
        List<PropagationPath> paths = propagationAnalysisService.analyzePropagationPaths(rumorId);
        
        int totalPaths = paths.size();
        double averagePathLength = paths.stream()
            .mapToInt(PropagationPath::getPathLength)
            .average()
            .orElse(0.0);
        int maxPathLength = paths.stream()
            .mapToInt(PropagationPath::getPathLength)
            .max()
            .orElse(0);
            
        return ResponseEntity.ok(Map.of(
            "totalPaths", totalPaths,
            "averagePathLength", averagePathLength,
            "maxPathLength", maxPathLength
        ));
    }

    @GetMapping("/network/{rumorId}")
    public ResponseEntity<Map<String, Object>> getPropagationNetwork(@PathVariable Long rumorId) {
        return ResponseEntity.ok(propagationAnalysisService.getPropagationNetwork(rumorId));
    }
} 