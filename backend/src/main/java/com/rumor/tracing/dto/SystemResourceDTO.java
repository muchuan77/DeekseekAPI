package com.rumor.tracing.dto;

import lombok.Data;

@Data
public class SystemResourceDTO {
    private Long totalMemory;  // 总内存
    private Long freeMemory;  // 空闲内存
    private Long maxMemory;  // 最大内存
    private Long totalDiskSpace;  // 总磁盘空间
    private Long freeDiskSpace;  // 空闲磁盘空间
    private Integer availableProcessors;  // 可用处理器数
    private String osName;  // 操作系统名称
    private String osVersion;  // 操作系统版本
    private String javaVersion;  // Java版本
    private Long uptime;  // 系统运行时间
} 