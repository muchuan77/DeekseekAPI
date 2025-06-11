package com.rumor.tracing;

import com.rumor.tracing.config.NoElasticsearchConfig;
import com.rumor.tracing.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import({NoElasticsearchConfig.class, TestConfig.class})
class ApplicationTests {

    @Test
    void contextLoads() {
        // 基础的上下文加载测试
    }

    @Test
    void basicTest() {
        // 基础测试，确保测试环境工作正常
        assert true;
    }
} 