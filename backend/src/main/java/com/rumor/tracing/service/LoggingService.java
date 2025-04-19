package com.rumor.tracing.service;

import com.rumor.tracing.dto.LogStatisticsDTO;
import com.rumor.tracing.dto.LogTrendDTO;
import com.rumor.tracing.dto.LogErrorDetailDTO;
import com.rumor.tracing.dto.LogServiceHealthDTO;
import com.rumor.tracing.entity.LogAudit;
import com.rumor.tracing.entity.LogOperation;
import com.rumor.tracing.entity.LogSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LoggingService {
    // 基础日志保存方法
    void saveOperationLog(LogOperation log);
    void saveSystemLog(LogSystem log);
    void saveAuditLog(LogAudit log);

    // 基础日志查询方法
    Page<LogOperation> searchOperationLogs(String username, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Page<LogSystem> searchSystemLogs(String level, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Page<LogAudit> searchAuditLogs(String username, String entityType, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // 新增的日志分析方法
    LogStatisticsDTO getLogStatistics(LocalDateTime start, LocalDateTime end);
    List<LogTrendDTO> getLogTrends(LocalDateTime start, LocalDateTime end, String interval);
    List<LogErrorDetailDTO> getErrorDetails(String type, LocalDateTime start, LocalDateTime end);
    LogServiceHealthDTO getServiceHealth(String name, LocalDateTime start, LocalDateTime end);
} 