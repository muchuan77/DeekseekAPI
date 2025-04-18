package com.rumor.tracing.repository;

import com.rumor.tracing.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    Page<OperationLog> findByOperationType(String operationType, Pageable pageable);
    Page<OperationLog> findByUsername(String username, Pageable pageable);
    Page<OperationLog> findByOperationTypeAndUsername(String operationType, String username, Pageable pageable);
} 