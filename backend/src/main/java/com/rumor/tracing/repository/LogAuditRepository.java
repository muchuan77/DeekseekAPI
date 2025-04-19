package com.rumor.tracing.repository;

import com.rumor.tracing.entity.LogAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LogAuditRepository extends ElasticsearchRepository<LogAudit, String> {
    Page<LogAudit> findByTargetTypeAndUsername(String targetType, String username, Pageable pageable);
    Page<LogAudit> findByTargetType(String targetType, Pageable pageable);
    Page<LogAudit> findByUsername(String username, Pageable pageable);
    Page<LogAudit> findByAuditTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
} 