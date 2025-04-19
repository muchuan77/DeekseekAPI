package com.rumor.tracing.repository;

import com.rumor.tracing.entity.LogOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LogOperationRepository extends ElasticsearchRepository<LogOperation, String> {
    Page<LogOperation> findByOperationTypeAndUsername(String operationType, String username, Pageable pageable);
    Page<LogOperation> findByOperationType(String operationType, Pageable pageable);
    Page<LogOperation> findByUsername(String username, Pageable pageable);
    Page<LogOperation> findByExecutionTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<LogOperation> findByOperationTypeAndExecutionTimeBetween(String operationType, LocalDateTime start, LocalDateTime end, Pageable pageable);
} 