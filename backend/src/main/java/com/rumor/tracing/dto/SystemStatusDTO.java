package com.rumor.tracing.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SystemStatusDTO {
    private String status;  // 系统状态：RUNNING, MAINTENANCE, ERROR
    private List<String> activeServices;  // 活跃服务列表
    private Map<String, String> serviceStatus;  // 服务状态映射
    private List<String> warnings;  // 警告信息
    private List<String> errors;  // 错误信息
    private LocalDateTime lastCheck;  // 最后检查时间
    private Boolean databaseConnected;  // 数据库连接状态
    private Boolean blockchainConnected;  // 区块链连接状态
    private Boolean aiServiceAvailable;  // AI服务可用状态
    private Map<String, Integer> serviceHealth;  // 服务健康度
} 