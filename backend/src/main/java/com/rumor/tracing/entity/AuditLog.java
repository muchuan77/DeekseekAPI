package com.rumor.tracing.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {
    
    @Column(nullable = false, length = 50)
    private String auditType;
    
    @Column(nullable = false, length = 50)
    private String targetType;
    
    @Column
    private Long targetId;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String beforeValue;
    
    @Column(columnDefinition = "TEXT")
    private String afterValue;
    
    @Column(length = 50)
    private String username;
    
    @Column(length = 50)
    private String ip;
    
    @Column(length = 500)
    private String userAgent;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @PrePersist
    protected void onPrePersist() {
        auditTime = LocalDateTime.now();
        if (status == null) {
            status = "SUCCESS";
        }
    }
} 