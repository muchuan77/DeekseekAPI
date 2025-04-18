package com.rumor.tracing.repository;

import com.rumor.tracing.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    Page<SystemLog> findByLevel(String level, Pageable pageable);
    Page<SystemLog> findByMessageContaining(String message, Pageable pageable);
    Page<SystemLog> findByLevelAndMessageContaining(String level, String message, Pageable pageable);
} 