package com.rumor.tracing.service;

import com.rumor.tracing.dto.response.RumorStatisticsResponse;
import com.rumor.tracing.dto.response.RumorTrendsResponse;

public interface AnalyticsService {
    RumorStatisticsResponse getStatistics(String timeRange);
    RumorTrendsResponse getTrends(String timeRange);
}
