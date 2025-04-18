package com.rumor.tracing.service;

import com.rumor.tracing.dto.*;
import java.util.List;

public interface SystemService {
    // 系统配置
    SystemConfigDTO getConfig();
    void updateConfig(SystemConfigDTO config);
    
    // 系统监控
    List<String> getSystemLogs();
    SystemMetricsDTO getSystemMetrics();
    SystemStatusDTO getSystemStatus();
    SystemResourceDTO getSystemResources();
    SystemMetricsDTO getSystemPerformance();
    
    // 告警管理
    List<String> getSystemAlerts();
    void updateAlertStatus(String id, String status);
} 