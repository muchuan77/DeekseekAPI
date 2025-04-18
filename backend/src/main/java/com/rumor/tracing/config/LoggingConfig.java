package com.rumor.tracing.config;

import com.rumor.tracing.aspect.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.rumor.tracing.repository.es")
public class LoggingConfig {

    @Bean
    public LoggingAspect loggingAspect(ElasticsearchOperations elasticsearchOperations) {
        return new LoggingAspect(elasticsearchOperations);
    }
}
