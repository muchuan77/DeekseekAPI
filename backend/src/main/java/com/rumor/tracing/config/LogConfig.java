package com.rumor.tracing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:log.properties")
public class LogConfig {
    // 日志相关配置
} 