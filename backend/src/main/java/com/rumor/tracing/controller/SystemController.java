package com.rumor.tracing.controller;

import com.rumor.tracing.dto.SystemConfigDTO;
import com.rumor.tracing.dto.SystemMetricsDTO;
import com.rumor.tracing.dto.SystemResourceDTO;
import com.rumor.tracing.dto.SystemStatusDTO;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "系统管理相关接口")
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/config")
    @Operation(summary = "获取系统配置")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> getConfig() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getConfig()));
    }

    @PutMapping("/config")
    @Operation(summary = "更新系统配置")
    public ResponseEntity<ApiResponse<Void>> updateConfig(@RequestBody SystemConfigDTO config) {
        systemService.updateConfig(config);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/logs")
    @Operation(summary = "获取系统日志")
    public ResponseEntity<ApiResponse<List<String>>> getSystemLogs() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getSystemLogs()));
    }

    @GetMapping("/metrics")
    @Operation(summary = "获取系统指标")
    public ResponseEntity<ApiResponse<SystemMetricsDTO>> getSystemMetrics() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getSystemMetrics()));
    }

    @GetMapping("/status")
    @Operation(summary = "获取系统状态")
    public ResponseEntity<ApiResponse<SystemStatusDTO>> getSystemStatus() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getSystemStatus()));
    }

    @GetMapping("/resources")
    @Operation(summary = "获取系统资源")
    public ResponseEntity<ApiResponse<SystemResourceDTO>> getSystemResources() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getSystemResources()));
    }

    @GetMapping("/performance")
    @Operation(summary = "获取系统性能")
    public ResponseEntity<ApiResponse<SystemMetricsDTO>> getSystemPerformance() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getSystemPerformance()));
    }

    @GetMapping("/alerts")
    @Operation(summary = "获取系统告警")
    public ResponseEntity<ApiResponse<List<String>>> getSystemAlerts() {
        return ResponseEntity.ok(ApiResponse.success(systemService.getSystemAlerts()));
    }

    @PutMapping("/alerts/{id}/status")
    @Operation(summary = "更新告警状态")
    public ResponseEntity<ApiResponse<Void>> updateAlertStatus(@PathVariable String id, @RequestParam String status) {
        systemService.updateAlertStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 