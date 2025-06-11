package com.rumor.tracing.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

import java.util.Optional;

/**
 * 测试环境配置类
 * 提供模拟的外部服务连接
 */
@TestConfiguration
@Profile("test")
@EnableJpaAuditing
public class TestConfig {

    /**
     * 模拟Redis连接工厂，避免测试时连接真实Redis
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }

    /**
     * 模拟RedisTemplate，避免测试时连接真实Redis
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        return mock(RedisTemplate.class);
    }

    @Bean
    @Primary
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test-user");
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return mock(RestTemplate.class);
    }
} 