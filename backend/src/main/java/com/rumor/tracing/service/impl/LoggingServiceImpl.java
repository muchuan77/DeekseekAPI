package com.rumor.tracing.service.impl;

import com.rumor.tracing.dto.LogStatisticsDTO;
import com.rumor.tracing.dto.LogTrendDTO;
import com.rumor.tracing.dto.LogErrorDetailDTO;
import com.rumor.tracing.dto.LogServiceHealthDTO;
import com.rumor.tracing.entity.LogAudit;
import com.rumor.tracing.entity.LogOperation;
import com.rumor.tracing.entity.LogSystem;
import com.rumor.tracing.service.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void saveOperationLog(LogOperation log) {
        elasticsearchOperations.save(log);
    }

    @Override
    public void saveSystemLog(LogSystem log) {
        elasticsearchOperations.save(log);
    }

    @Override
    public void saveAuditLog(LogAudit log) {
        elasticsearchOperations.save(log);
    }

    @Override
    public Page<LogOperation> searchOperationLogs(String username, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Criteria criteria = new Criteria();
        
        if (username != null && !username.isEmpty()) {
            criteria.and("username").is(username);
        }
        
        if (startTime != null && endTime != null) {
            criteria.and("executionTime").between(startTime, endTime);
        }
        
        CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<LogOperation> searchHits = elasticsearchOperations.search(query, LogOperation.class);
        
        List<LogOperation> logs = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
                
        return new PageImpl<>(logs, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<LogSystem> searchSystemLogs(String level, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Criteria criteria = new Criteria();
        
        if (level != null && !level.isEmpty()) {
            criteria.and("level").is(level);
        }
        
        if (startTime != null && endTime != null) {
            criteria.and("logTime").between(startTime, endTime);
        }
        
        CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<LogSystem> searchHits = elasticsearchOperations.search(query, LogSystem.class);
        
        List<LogSystem> logs = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
                
        return new PageImpl<>(logs, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<LogAudit> searchAuditLogs(String username, String entityType, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Criteria criteria = new Criteria();
        
        if (username != null && !username.isEmpty()) {
            criteria.and("username").is(username);
        }
        
        if (entityType != null && !entityType.isEmpty()) {
            criteria.and("targetType").is(entityType);
        }
        
        if (startTime != null && endTime != null) {
            criteria.and("auditTime").between(startTime, endTime);
        }
        
        CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<LogAudit> searchHits = elasticsearchOperations.search(query, LogAudit.class);
        
        List<LogAudit> logs = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
                
        return new PageImpl<>(logs, pageable, searchHits.getTotalHits());
    }

    @Override
    public LogStatisticsDTO getLogStatistics(LocalDateTime start, LocalDateTime end) {
        // 创建查询条件
        Criteria criteria = new Criteria("executionTime").between(start, end);
        
        // 创建查询
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        // 执行操作日志查询
        SearchHits<LogOperation> operationHits = elasticsearchOperations.search(query, LogOperation.class);
        
        // 创建统计结果对象
        LogStatisticsDTO statistics = new LogStatisticsDTO();
        statistics.setTotalLogs(operationHits.getTotalHits());
        
        // 手动统计操作类型
        Map<String, Long> operationTypeCounts = operationHits.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.groupingBy(
                LogOperation::getOperationType,
                Collectors.counting()
            ));
        statistics.setOperationTypeCounts(operationTypeCounts);
        
        // 手动统计用户活跃度
        Map<String, Long> userActivityCounts = operationHits.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.groupingBy(
                LogOperation::getUsername,
                Collectors.counting()
            ));
        statistics.setUserActivityCounts(userActivityCounts);
        
        // 查询系统日志以获取日志级别统计
        Criteria systemCriteria = new Criteria("logTime").between(start, end);
        CriteriaQuery systemQuery = new CriteriaQuery(systemCriteria);
        SearchHits<LogSystem> systemHits = elasticsearchOperations.search(systemQuery, LogSystem.class);
        
        // 统计日志级别
        long errorLogs = systemHits.stream()
            .map(SearchHit::getContent)
            .filter(log -> "ERROR".equals(log.getLevel()))
            .count();
        statistics.setErrorLogs(errorLogs);
        
        long warningLogs = systemHits.stream()
            .map(SearchHit::getContent)
            .filter(log -> "WARN".equals(log.getLevel()))
            .count();
        statistics.setWarningLogs(warningLogs);
        
        long infoLogs = systemHits.stream()
            .map(SearchHit::getContent)
            .filter(log -> "INFO".equals(log.getLevel()))
            .count();
        statistics.setInfoLogs(infoLogs);
        
        return statistics;
    }

    @Override
    public List<LogTrendDTO> getLogTrends(LocalDateTime start, LocalDateTime end, String interval) {
        DateHistogramInterval dateInterval;
        switch (interval.toLowerCase()) {
            case "hour":
                dateInterval = DateHistogramInterval.HOUR;
                break;
            case "day":
                dateInterval = DateHistogramInterval.DAY;
                break;
            case "week":
                dateInterval = DateHistogramInterval.WEEK;
                break;
            case "month":
                dateInterval = DateHistogramInterval.MONTH;
                break;
            default:
                dateInterval = DateHistogramInterval.DAY;
        }

        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.rangeQuery("executionTime")
                .gte(start)
                .lte(end))
            .addAggregation(AggregationBuilders.dateHistogram("trends")
                .field("executionTime")
                .fixedInterval(dateInterval))
            .build();
            
        SearchHits<LogOperation> searchHits = elasticsearchOperations.search(query, LogOperation.class);
        
        // 处理趋势数据
        if (searchHits.hasAggregations()) {
            var aggregationsContainer = searchHits.getAggregations();
            var aggregations = (Aggregations) aggregationsContainer.aggregations();
            Histogram trendsAgg = (Histogram) aggregations.get("trends");
            if (trendsAgg != null) {
                return trendsAgg.getBuckets().stream()
                    .map(bucket -> {
                        LogTrendDTO dto = new LogTrendDTO();
                        dto.setTimestamp(LocalDateTime.parse(bucket.getKeyAsString()));
                        dto.setCount(bucket.getDocCount());
                        dto.setType("operation");
                        return dto;
                    })
                    .collect(Collectors.toList());
            }
        }
        
        return List.of();
    }

    @Override
    public List<LogErrorDetailDTO> getErrorDetails(String type, LocalDateTime start, LocalDateTime end) {
        Criteria criteria = new Criteria("level").is("ERROR")
                .and("type").is(type)
                .and("logTime").between(start, end);
        
        SearchHits<LogSystem> searchHits = elasticsearchOperations.search(
                new CriteriaQuery(criteria), LogSystem.class);
        
        return searchHits.stream()
                .map(hit -> {
                    LogSystem log = hit.getContent();
                    LogErrorDetailDTO dto = new LogErrorDetailDTO();
                    dto.setTimestamp(log.getLogTime());
                    dto.setType(type);
                    dto.setMessage(log.getMessage());
                    dto.setStackTrace(log.getStackTrace());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public LogServiceHealthDTO getServiceHealth(String name, LocalDateTime start, LocalDateTime end) {
        // 查询指定服务的日志
        Criteria criteria = new Criteria("applicationName").is(name)
                .and("logTime").between(start, end);
        
        SearchHits<LogSystem> searchHits = elasticsearchOperations.search(
                new CriteriaQuery(criteria), LogSystem.class);
        
        LogServiceHealthDTO health = new LogServiceHealthDTO();
        health.setServiceName(name);
        
        // 计算服务健康状态
        long totalLogs = searchHits.getTotalHits();
        long errorLogs = searchHits.stream()
                .filter(hit -> "ERROR".equals(hit.getContent().getLevel()))
                .count();
        
        health.setErrorCount((int) errorLogs);
        health.setAvailability(totalLogs > 0 ? (double) (totalLogs - errorLogs) / totalLogs * 100 : 100.0);
        
        // 计算平均响应时间
        double avgResponseTime = searchHits.stream()
                .mapToLong(hit -> hit.getContent().getResponseTime() != null ? hit.getContent().getResponseTime() : 0)
                .average()
                .orElse(0.0);
        
        health.setResponseTime(avgResponseTime);
        
        return health;
    }
} 