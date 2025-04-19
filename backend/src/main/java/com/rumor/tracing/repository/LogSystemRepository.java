package com.rumor.tracing.repository;

import com.rumor.tracing.entity.LogSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LogSystemRepository extends ElasticsearchRepository<LogSystem, String> {
    Page<LogSystem> findByLevel(String level, Pageable pageable);
    Page<LogSystem> findByApplicationName(String applicationName, Pageable pageable);
    Page<LogSystem> findByLogTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<LogSystem> findByLevelAndLogTimeBetween(String level, LocalDateTime start, LocalDateTime end, Pageable pageable);
} 