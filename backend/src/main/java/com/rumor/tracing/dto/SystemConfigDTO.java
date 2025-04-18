package com.rumor.tracing.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SystemConfigDTO {
    private String systemName;  // 系统名称
    private String version;  // 系统版本
    private String description;  // 系统描述
    private String logo;  // 系统logo URL
    private Boolean maintenance;  // 是否维护模式
    private Map<String, String> emailConfig;  // 邮件配置
    private Map<String, String> storageConfig;  // 存储配置
    private Map<String, String> blockchainConfig;  // 区块链配置
    private Map<String, Object> aiConfig;  // AI配置
    private Integer maxUploadSize;  // 最大上传大小(MB)
    private Integer sessionTimeout;  // 会话超时时间(分钟)
    private Boolean debugMode;  // 调试模式
} 