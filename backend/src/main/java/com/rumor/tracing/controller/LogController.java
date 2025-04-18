package com.rumor.tracing.controller;

import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.dto.response.PageResponse;
import com.rumor.tracing.entity.AuditLog;
import com.rumor.tracing.entity.OperationLog;
import com.rumor.tracing.entity.SystemLog;
import com.rumor.tracing.repository.AuditLogRepository;
import com.rumor.tracing.repository.OperationLogRepository;
import com.rumor.tracing.repository.SystemLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "日志管理", description = "系统日志管理相关接口")
public class LogController {

    private final SystemLogRepository systemLogRepository;
    private final OperationLogRepository operationLogRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/system")
    @Operation(summary = "获取系统日志", description = "分页获取系统日志信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<PageResponse<SystemLog>>> getSystemLogs(
            @Parameter(description = "日志级别") @RequestParam(required = false) String level,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SystemLog> logPage;
        
        if (level != null && !level.isEmpty()) {
            logPage = systemLogRepository.findByLevel(level, pageRequest);
        } else {
            logPage = systemLogRepository.findAll(pageRequest);
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(logPage)));
    }

    @GetMapping("/operation")
    @Operation(summary = "获取操作日志", description = "分页获取操作日志信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<PageResponse<OperationLog>>> getOperationLogs(
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OperationLog> logPage;
        
        if (operationType != null && username != null) {
            logPage = operationLogRepository.findByOperationTypeAndUsername(operationType, username, pageRequest);
        } else if (operationType != null) {
            logPage = operationLogRepository.findByOperationType(operationType, pageRequest);
        } else if (username != null) {
            logPage = operationLogRepository.findByUsername(username, pageRequest);
        } else {
            logPage = operationLogRepository.findAll(pageRequest);
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(logPage)));
    }

    @GetMapping("/audit")
    @Operation(summary = "获取审计日志", description = "分页获取审计日志信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getAuditLogs(
            @Parameter(description = "审计类型") @RequestParam(required = false) String auditType,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLog> logPage;
        
        if (auditType != null && username != null) {
            logPage = auditLogRepository.findByAuditTypeAndUsername(auditType, username, pageRequest);
        } else if (auditType != null) {
            logPage = auditLogRepository.findByAuditType(auditType, pageRequest);
        } else if (username != null) {
            logPage = auditLogRepository.findByUsername(username, pageRequest);
        } else {
            logPage = auditLogRepository.findAll(pageRequest);
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(logPage)));
    }
} 