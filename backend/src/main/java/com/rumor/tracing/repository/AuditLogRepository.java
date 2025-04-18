package com.rumor.tracing.repository;

import com.rumor.tracing.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByAuditType(String auditType, Pageable pageable);
    Page<AuditLog> findByUsername(String username, Pageable pageable);
    Page<AuditLog> findByAuditTypeAndUsername(String auditType, String username, Pageable pageable);
} 