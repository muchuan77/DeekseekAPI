package com.rumor.tracing;

import com.rumor.tracing.config.NoElasticsearchConfig;
import com.rumor.tracing.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.rumor.tracing.repository.LogOperationRepository;
import com.rumor.tracing.repository.LogAuditRepository;
import com.rumor.tracing.repository.LogSystemRepository;

@SpringBootTest
@ActiveProfiles("test")
@Import({NoElasticsearchConfig.class, TestConfig.class})
class ApplicationTests {

    @MockBean
    private LogOperationRepository logOperationRepository;

    @MockBean
    private LogAuditRepository logAuditRepository;

    @MockBean
    private LogSystemRepository logSystemRepository;

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