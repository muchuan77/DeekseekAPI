package com.rumor.tracing.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SystemMetricsDTO {
    private Double cpuUsage;  // CPU使用率
    private Double memoryUsage;  // 内存使用率
    private Long diskUsage;  // 磁盘使用量
    private Integer activeUsers;  // 活跃用户数
    private Integer requestsPerMinute;  // 每分钟请求数
    private Double averageResponseTime;  // 平均响应时间
    private Integer errorCount;  // 错误数
    private Map<String, Integer> apiUsage;  // API使用统计
    private Double systemLoad;  // 系统负载
    private Integer threadCount;  // 线程数
    private Long heapMemory;  // 堆内存使用
    private Long nonHeapMemory;  // 非堆内存使用
} 