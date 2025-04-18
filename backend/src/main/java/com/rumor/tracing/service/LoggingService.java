package com.rumor.tracing.service;

import com.rumor.tracing.entity.es.AuditLog;
import com.rumor.tracing.entity.es.OperationLog;
import com.rumor.tracing.entity.es.SystemLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final ElasticsearchOperations elasticsearchOperations;

    public void saveOperationLog(OperationLog log) {
        elasticsearchOperations.save(log);
    }

    public void saveSystemLog(SystemLog log) {
        elasticsearchOperations.save(log);
    }

    public void saveAuditLog(AuditLog log) {
        elasticsearchOperations.save(log);
    }

    public List<OperationLog> searchOperationLogs(String username, LocalDateTime startTime, 
            LocalDateTime endTime) {
        Criteria criteria = new Criteria("username").is(username)
                .and("timestamp").between(startTime, endTime);
        
        SearchHits<OperationLog> searchHits = elasticsearchOperations.search(
                new CriteriaQuery(criteria), OperationLog.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public List<SystemLog> searchSystemLogs(String level, LocalDateTime startTime, 
            LocalDateTime endTime) {
        Criteria criteria = new Criteria("level").is(level)
                .and("timestamp").between(startTime, endTime);
        
        SearchHits<SystemLog> searchHits = elasticsearchOperations.search(
                new CriteriaQuery(criteria), SystemLog.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public List<AuditLog> searchAuditLogs(String username, String entityType, 
            LocalDateTime startTime, LocalDateTime endTime) {
        Criteria criteria = new Criteria("username").is(username)
                .and("entityType").is(entityType)
                .and("timestamp").between(startTime, endTime);
        
        SearchHits<AuditLog> searchHits = elasticsearchOperations.search(
                new CriteriaQuery(criteria), AuditLog.class);
        
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
} 