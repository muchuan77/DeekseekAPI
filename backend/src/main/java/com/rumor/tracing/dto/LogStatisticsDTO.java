package com.rumor.tracing.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LogStatisticsDTO {
    private long totalLogs;
    private long errorLogs;
    private long warningLogs;
    private long infoLogs;
    private Map<String, Long> operationTypeCounts;
    private Map<String, Long> userActivityCounts;
}