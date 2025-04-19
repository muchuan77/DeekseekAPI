package com.rumor.tracing.controller;

import com.rumor.tracing.dto.LogStatisticsDTO;
import com.rumor.tracing.dto.LogTrendDTO;
import com.rumor.tracing.dto.LogErrorDetailDTO;
import com.rumor.tracing.dto.LogServiceHealthDTO;
import com.rumor.tracing.entity.LogAudit;
import com.rumor.tracing.entity.LogOperation;
import com.rumor.tracing.entity.LogSystem;
import com.rumor.tracing.service.LoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "日志管理", description = "日志管理相关接口")
public class LogController {

    private final LoggingService loggingService;

    @PostMapping("/operation")
    @Operation(summary = "Save operation log", description = "Save a new operation log entry")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Log saved successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid log data")
    public ResponseEntity<Void> saveOperationLog(@RequestBody LogOperation log) {
        loggingService.saveOperationLog(log);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/system")
    @Operation(summary = "Save system log", description = "Save a new system log entry")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Log saved successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid log data")
    public ResponseEntity<Void> saveSystemLog(@RequestBody LogSystem log) {
        loggingService.saveSystemLog(log);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/audit")
    @Operation(summary = "Save audit log", description = "Save a new audit log entry")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Log saved successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid log data")
    public ResponseEntity<Void> saveAuditLog(@RequestBody LogAudit log) {
        loggingService.saveAuditLog(log);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/operation")
    @Operation(summary = "Search operation logs", description = "Search operation logs with filters")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logs retrieved successfully", 
                content = @Content(schema = @Schema(implementation = Page.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<Page<LogOperation>> searchOperationLogs(
            @Parameter(description = "Username to filter by") @RequestParam(required = false) String username,
            @Parameter(description = "Start time for filtering") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time for filtering") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        return ResponseEntity.ok(loggingService.searchOperationLogs(username, startTime, endTime, pageable));
    }

    @GetMapping("/system")
    @Operation(summary = "Search system logs", description = "Search system logs with filters")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logs retrieved successfully",
                content = @Content(schema = @Schema(implementation = Page.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<Page<LogSystem>> searchSystemLogs(
            @Parameter(description = "Log level to filter by") @RequestParam(required = false) String level,
            @Parameter(description = "Start time for filtering") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time for filtering") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        return ResponseEntity.ok(loggingService.searchSystemLogs(level, startTime, endTime, pageable));
    }

    @GetMapping("/audit")
    @Operation(summary = "Search audit logs", description = "Search audit logs with filters")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logs retrieved successfully",
                content = @Content(schema = @Schema(implementation = Page.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<Page<LogAudit>> searchAuditLogs(
            @Parameter(description = "Username to filter by") @RequestParam(required = false) String username,
            @Parameter(description = "Entity type to filter by") @RequestParam(required = false) String entityType,
            @Parameter(description = "Start time for filtering") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time for filtering") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        return ResponseEntity.ok(loggingService.searchAuditLogs(username, entityType, startTime, endTime, pageable));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get log statistics", description = "Get aggregated statistics for logs")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                content = @Content(schema = @Schema(implementation = LogStatisticsDTO.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<LogStatisticsDTO> getLogStatistics(
            @Parameter(description = "Start time for statistics") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time for statistics") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(loggingService.getLogStatistics(start, end));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get log trends", description = "Get log trends over time")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trends retrieved successfully",
                content = @Content(schema = @Schema(implementation = LogTrendDTO.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<List<LogTrendDTO>> getLogTrends(
            @Parameter(description = "Start time for trends") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time for trends") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(description = "Time interval for aggregation") @RequestParam(defaultValue = "day") String interval) {
        return ResponseEntity.ok(loggingService.getLogTrends(start, end, interval));
    }

    @GetMapping("/errors/{type}")
    @Operation(summary = "Get error details", description = "Get detailed information about errors")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Error details retrieved successfully",
                content = @Content(schema = @Schema(implementation = LogErrorDetailDTO.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<List<LogErrorDetailDTO>> getErrorDetails(
            @Parameter(description = "Error type") @PathVariable String type,
            @Parameter(description = "Start time for filtering") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time for filtering") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(loggingService.getErrorDetails(type, start, end));
    }

    @GetMapping("/services/{name}/health")
    @Operation(summary = "Get service health", description = "Get health status of a service")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Service health retrieved successfully",
                content = @Content(schema = @Schema(implementation = LogServiceHealthDTO.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<LogServiceHealthDTO> getServiceHealth(
            @Parameter(description = "Service name") @PathVariable String name,
            @Parameter(description = "Start time for health check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time for health check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(loggingService.getServiceHealth(name, start, end));
    }
}