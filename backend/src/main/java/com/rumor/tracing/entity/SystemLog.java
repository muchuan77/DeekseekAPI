package com.rumor.tracing.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SystemLog extends BaseEntity {
    
    @Column(nullable = false, length = 20)
    private String level;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(columnDefinition = "TEXT")
    private String exception;
    
    @Column(length = 100)
    private String logger;
    
    @Column(length = 50)
    private String thread;
    
    @Column(length = 50)
    private String ip;
    
    @Column(length = 500)
    private String userAgent;

    @Column(name = "log_time")
    private LocalDateTime logTime;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "application_name", length = 50)
    private String applicationName;

    @PrePersist
    protected void onPrePersist() {
        logTime = LocalDateTime.now();
    }
} 