package com.rumor.tracing.config;

import com.rumor.tracing.repository.LogOperationRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class NoElasticsearchConfig {
    @Bean
    @Primary
    public LogOperationRepository logOperationRepository() {
        return Mockito.mock(LogOperationRepository.class);
    }
} 