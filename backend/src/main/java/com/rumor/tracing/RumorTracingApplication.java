package com.rumor.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RumorTracingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RumorTracingApplication.class, args);
    }
}